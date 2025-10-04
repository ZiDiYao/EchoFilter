package com.echofilter.lowerLevel.infrastructure.modules.dto.request;

import java.util.List;

public record BatchAnalyzeRequest(
        String taskId,                 // 可选：前端指定；为空则后端生成
        String correlationId,          // 可选：链路追踪；为空则后端生成
        String llmApi,                 // 这批任务选用的模型名（可覆盖单条里的 LlmApi）
        List<LlmPromptInput> comments  // 多条评论
) {}