package com.echofilter.lowerLevel.infrastructure.modules.domain;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AnalysisResult {
    private String id;                 // 我们生成
    private String targetType;         // "POST" | "COMMENT"
    private String targetId;           // Post.id 或 Comment.id
    private String requestFingerprint; // 版本/模型/超参/模板指纹(哈希)
    private String modelResolved;      // 真实模型名，如 "gpt-4o-mini"
    private String promptVersion;      // 如 "promptV3"
    private String resultJson;         // 分析结果
    private String type; // misinformation, discussion...
    private BigDecimal trustScore;          // 置信度/风险等（可选）
    private LocalDateTime createdAt = LocalDateTime.now();
}
