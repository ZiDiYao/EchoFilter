package com.echofilter.infrastructure.messaging.kafka.producer;

import com.echofilter.infrastructure.messaging.kafka.model.AnalyzeRequestEvent;
import com.echofilter.infrastructure.messaging.kafka.topics.KafkaTopics;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestProducer {
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper om;

    public void send(AnalyzeRequestEvent evt) {
        var key = evt.commentId() != null ? evt.commentId() : evt.taskId();
        var payload = toJson(evt);

        Message<String> msg = MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, KafkaTopics.REQ)
                .setHeader(KafkaHeaders.KEY, key)
                .setHeader("x-correlation-id", evt.correlationId())
                .setHeader("x-schema", evt.version())
                .build();

        kafka.send(msg);
    }

    private String toJson(Object o) {
        try { return om.writeValueAsString(o); }
        catch (Exception e) { throw new IllegalStateException("serialize event failed", e); }
    }
}
