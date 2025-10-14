package com.echofilter.lowerLevel.infrastructure.messaging.kafka.producer;

import com.echofilter.lowerLevel.infrastructure.messaging.kafka.model.AnalyzeResultEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResultProducer {

    private static final String RESULT_TOPIC = "ef.results.v1";

    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper om;

    public void send(AnalyzeResultEvent evt,
                     String eventId,
                     String taskId,
                     String commentId,
                     String platform,
                     String schema,
                     String correlationId,
                     String key // 建议用 commentId 或 postId:commentId
    ) {
        try {
            String payload = om.writeValueAsString(evt);
            ProducerRecord<String, String> record = new ProducerRecord<>(RESULT_TOPIC, key, payload);

            // 透传可观测 headers
            if (eventId != null)      record.headers().add("x-event-id", eventId.getBytes(StandardCharsets.UTF_8));
            if (taskId != null)       record.headers().add("x-task-id", taskId.getBytes(StandardCharsets.UTF_8));
            if (commentId != null)    record.headers().add("x-comment-id", commentId.getBytes(StandardCharsets.UTF_8));
            if (platform != null)     record.headers().add("x-platform", platform.getBytes(StandardCharsets.UTF_8));
            if (correlationId != null)record.headers().add("x-correlation-id", correlationId.getBytes(StandardCharsets.UTF_8));
            if (schema != null)       record.headers().add("x-schema", schema.getBytes(StandardCharsets.UTF_8));
            record.headers().add("x-processed-at", Instant.now().toString().getBytes(StandardCharsets.UTF_8));

            kafka.send(record).whenComplete((meta, ex) -> {
                if (ex == null) {
                    log.info("[RES] published topic={} partition={} offset={}",
                            RESULT_TOPIC, meta.getRecordMetadata().partition(), meta.getRecordMetadata().offset());
                } else {
                    log.error("[RES] publish failed key={}, err={}", key, ex.toString(), ex);
                }
            });

        } catch (Exception e) {
            log.error("[RES] serialization/send failed, err={}", e.toString(), e);
        }
    }
}
