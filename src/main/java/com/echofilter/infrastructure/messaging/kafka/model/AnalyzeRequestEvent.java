package com.echofilter.infrastructure.messaging.kafka.model;

import java.time.Instant;

public record AnalyzeRequestEvent(
        String version,        // "req.v1"
        String eventId,        // UUID：幂等键
        String correlationId,  // 跟踪
        String taskId,         // 批任务ID
        String commentId,      // 来自 LlmPromptInput，可用 null 时用 hash
        String platform,
        String language,
        String text,
        String context,
        String region,
        Boolean requiresFreshEvidence,
        Integer freshnessWindowHours,
        String llmApi,
        Instant createdAt,
        ProducerMeta producer
) {
    public record ProducerMeta(String service, String version) {}
}