package com.echofilter.lowerLevel.infrastructure.messaging.kafka.model;

import com.echofilter.lowerLevel.infrastructure.modules.dto.response.AnalysisResponse;
import java.time.Instant;

/**
 * AnalyzeResultEvent
 * ------------------
 * 这是 EchoFilter 的 LLM 分析结果事件模型，用于从 MLRequestListener
 * 向 Kafka 结果通道 (ef.results.v1) 发送最终识别结果。
 */
public record AnalyzeResultEvent(
        String version,         // "res.v1" - 当前事件版本
        String correlationId,   // 链路追踪ID
        String taskId,          // 批任务ID（同一批评论共享）
        String commentId,       // 评论唯一ID
        String platform,        // 平台名 (e.g., Reddit, Twitter)
        String language,        // 评论语言
        AnalysisResponse result,// LLM 分析结果
        String llmApi,          // 实际调用的模型 (e.g., "deepseek")
        Instant processedAt     // 处理时间
) {}