package com.echofilter.lowerLevel.infrastructure.messaging.kafka.consumer;


import com.echofilter.lowerLevel.infrastructure.messaging.kafka.model.AnalyzeResultEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResultDebugListener {

    private final ObjectMapper om;

    @KafkaListener(topics = "ef.results.v1", groupId = "ef-results-debug")
    public void onResult(
            String payload,
            @Header(name = "x-correlation-id", required = false) String corr,
            @Header(name = "x-task-id", required = false) String task,
            @Header(name = "x-comment-id", required = false) String commentId
    ) {
        try {
            AnalyzeResultEvent evt = om.readValue(payload, AnalyzeResultEvent.class);
            log.info("[RESULT] taskId={}, commentId={}, platform={}, type={}, conf={}, trust={}, facts={}",
                    evt.taskId(),
                    evt.commentId(),
                    evt.platform(),
                    evt.result().getType(),
                    evt.result().getConfidence(),
                    evt.result().getTrustScore(),
                    evt.result().getFacts());
        } catch (Exception e) {
            log.error("[RESULT] bad payload: {}", payload, e);
        }
    }
}
