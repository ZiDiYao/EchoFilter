package com.echofilter.lowerLevel.infrastructure.messaging.kafka.consumer;

import com.echofilter.lowerLevel.infrastructure.messaging.kafka.model.AnalyzeRequestEvent;
import com.echofilter.lowerLevel.infrastructure.modules.dto.request.LlmPromptInput;
import com.echofilter.lowerLevel.infrastructure.modules.service.CommentAnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

@Slf4j
@Component
@RequiredArgsConstructor
public class MLRequestListener {

    private final ObjectMapper om;
    @Qualifier("commentAnalysisServiceImpl")
    private final CommentAnalysisService analysisService;

    // 可选：有就用来限流（避免把 LLM 打爆）；没有也可以去掉
    private final ExecutorService virtualThreadExecutor;
    private final Semaphore llmSlots;

    @KafkaListener(topics = "ef.requests.v1", groupId = "echofilter-group")
    public void onMessage(String payload) {
        // 监听器线程只做轻活：解析 + 提交给虚拟线程
        virtualThreadExecutor.submit(() -> handleOne(payload));
    }

    private void handleOne(String payload) {
        try {
            // 1) 反序列化请求
            AnalyzeRequestEvent req = om.readValue(payload, AnalyzeRequestEvent.class);

            // 2) 映射为 LlmPromptInput（含 Constraints）
            LlmPromptInput input = new LlmPromptInput(
                    req.platform(),
                    req.language(),
                    req.text(),
                    req.context(),
                    new LlmPromptInput.Constraints(req.region(), req.requiresFreshEvidence(), req.freshnessWindowHours()),
                    req.llmApi()  // 注意：传给 LlmPromptInput 的 LlmApi 字段（首字母大写的访问器）
            );

            // 3) 调用 LLM（最小目标：只要能打到 API）
            acquire(); // 你有并发门槛就保留；没有可删
            var result = analysisService.getCommentResult(input);

            // 4) 日志（通用：把整个结果序列化，避免依赖具体字段）
            String resultJson = safeJson(result);
            log.info("[LLM] OK eventId={}, taskId={}, commentId={}, platform={}, result={}",
                    req.eventId(), req.taskId(), req.commentId(), req.platform(), resultJson);

        } catch (com.fasterxml.jackson.core.JsonProcessingException je) {
            log.error("[LLM] bad request payload. raw={}, err={}", trim(payload), je.toString(), je);
        } catch (Exception e) {
            log.error("[LLM] processing failed. raw={}, err={}", trim(payload), e.toString(), e);
        } finally {
            release();  // 放 finally，避免异常路径忘记释放
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
