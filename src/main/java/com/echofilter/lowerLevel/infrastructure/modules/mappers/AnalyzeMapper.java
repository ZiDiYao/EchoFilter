package com.echofilter.lowerLevel.infrastructure.modules.mappers;

import com.echofilter.lowerLevel.infrastructure.messaging.kafka.model.AnalyzeRequestEvent;
import com.echofilter.lowerLevel.infrastructure.modules.dto.request.LlmPromptInput;

import java.time.Instant;
import java.util.UUID;

public class AnalyzeMapper {

    public static AnalyzeRequestEvent toEvent(
            String taskId, String correlationId, String overrideLlmApi, LlmPromptInput in) {

        var llm = (overrideLlmApi != null && !overrideLlmApi.isBlank())
                ? overrideLlmApi : in.LlmApi();

        return new AnalyzeRequestEvent(
                "req.v1",
                UUID.randomUUID().toString(),
                correlationId,
                taskId,
                /* commentId */ in.content() != null ? Integer.toHexString(in.content().hashCode()) : null,
                in.platform(),
                in.language(),
                in.content(),
                in.context(),
                in.constraints() != null ? in.constraints().region() : null,
                in.constraints() != null ? in.constraints().requiresFreshEvidence() : null,
                in.constraints() != null ? in.constraints().freshnessWindowHours() : null,
                llm,
                Instant.now(),
                new AnalyzeRequestEvent.ProducerMeta("bff", "1.0.0")
        );
    }
}