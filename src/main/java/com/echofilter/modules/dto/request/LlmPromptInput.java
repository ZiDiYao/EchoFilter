package com.echofilter.modules.dto.request;

public record LlmPromptInput(
        String platform,            // e.g. "Reddit"
        String language,            // e.g. "en"
        String content,             // 归一化 + 裁剪后的评论正文
        String context,             // 归一化 + 裁剪后的原帖（可为 null/空）
        Constraints constraints,     // 任务级约束提示（不含时间戳/URL）
        String LlmApi
) {
    public record Constraints(
            String region,                // e.g. "US"
            Boolean requiresFreshEvidence,// e.g. true
            Integer freshnessWindowHours  // e.g. 72
    ) {}
}