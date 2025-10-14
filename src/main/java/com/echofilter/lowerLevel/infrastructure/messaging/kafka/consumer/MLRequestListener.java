package com.echofilter.lowerLevel.infrastructure.messaging.kafka.consumer;

import com.echofilter.lowerLevel.infrastructure.messaging.kafka.model.AnalyzeRequestEvent;
import com.echofilter.lowerLevel.infrastructure.messaging.kafka.model.AnalyzeResultEvent;
import com.echofilter.lowerLevel.infrastructure.messaging.kafka.producer.ResultProducer;
import com.echofilter.lowerLevel.infrastructure.modules.dto.request.LlmPromptInput;
import com.echofilter.lowerLevel.infrastructure.modules.dto.response.AnalysisResponse;
import com.echofilter.lowerLevel.infrastructure.modules.service.CommentAnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

@Slf4j
@Component
@RequiredArgsConstructor
public class MLRequestListener {

    private final ObjectMapper om;
    @Qualifier("commentAnalysisServiceImpl")
    private final CommentAnalysisService analysisService;

    private final ExecutorService virtualThreadExecutor;
    private final Semaphore llmSlots;

    private final ResultProducer resultProducer; // ✅ 注入结果生产者

    @KafkaListener(topics = "ef.requests.v1", groupId = "echofilter-group")
    public void onMessage(String payload) {
        virtualThreadExecutor.submit(() -> handleOne(payload));
    }

    private void handleOne(String payload) {
        try {
            AnalyzeRequestEvent req = om.readValue(payload, AnalyzeRequestEvent.class);

            LlmPromptInput input = new LlmPromptInput(
                    req.platform(),
                    req.language(),
                    req.text(),
                    req.context(),
                    new LlmPromptInput.Constraints(req.region(), req.requiresFreshEvidence(), req.freshnessWindowHours()),
                    req.llmApi()
            );

            acquire();
            AnalysisResponse result = analysisService.getCommentResult(input);

            // 本地日志
            String resultJson = safeJson(result);
            log.info("[LLM] OK eventId={}, taskId={}, commentId={}, platform={}, result={}",
                    req.eventId(), req.taskId(), req.commentId(), req.platform(), resultJson);

            // 组装并发送结果事件 -> ef.results.v1
            AnalyzeResultEvent outEvt = new AnalyzeResultEvent(
                    "res.v1",
                    req.correlationId(),
                    req.taskId(),
                    req.commentId(),
                    req.platform(),
                    req.language(),
                    result,
                    req.llmApi(),
                    Instant.now()
            );

            // key 建议：优先 commentId；若为空则退回 taskId
            String key = (req.commentId() != null && !req.commentId().isBlank())
                    ? req.commentId()
                    : req.taskId();

            resultProducer.send(
                    outEvt,
                    req.eventId(),
                    req.taskId(),
                    req.commentId(),
                    req.platform(),
                    req.version(),
                    req.correlationId(),
                    key
            );

        } catch (com.fasterxml.jackson.core.JsonProcessingException je) {
            log.error("[LLM] bad request payload. raw={}, err={}", trim(payload), je.toString(), je);
        } catch (Exception e) {
            log.error("[LLM] processing failed. raw={}, err={}", trim(payload), e.toString(), e);
        } finally {
            release();
        }
    }

    private String safeJson(Object o) {
        try { return om.writeValueAsString(o); }
        catch (Exception e) { return "<unserializable:" + e.getClass().getSimpleName() + ">"; }
    }

    private void acquire() {
        if (llmSlots != null) {
            try { llmSlots.acquire(); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        }
    }
    private void release() {
        if (llmSlots != null) llmSlots.release();
    }

    private static String trim(String s) {
        if (s == null) return "null";
        return s.length() > 400 ? s.substring(0, 400) + "...(truncated)" : s;
    }
}
