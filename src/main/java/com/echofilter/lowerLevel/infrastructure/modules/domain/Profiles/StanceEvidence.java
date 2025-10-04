package com.echofilter.lowerLevel.infrastructure.modules.domain.Profiles;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StanceEvidence {
    private String id;                     // ULID/UUID
    private String accountId;
    private String issueCode;
    private String targetType;             // "POST" / "COMMENT"
    private String targetId;               // Post.id 或 Comment.id
    private String contentHash;            // 原文哈希（不存原文，合规）
    private String snippetHash;            // 片段哈希（选）
    private String method;                 // LLM/regex/rule/vision_phash...
    private BigDecimal weight;             // 证据权重
    private BigDecimal confidence;         // 证据置信度
    private String rationale;              // 简短理由/命中特征名
    private LocalDateTime atTime;          // 言论时间（若能取到）
    private LocalDateTime gatheredAt;      // 我们采集到的时间
    private String requestFingerprint;     // 与上游一致
}
