package com.echofilter.lowerLevel.infrastructure.modules.domain.Profiles;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AccountProfile {
    private String accountId;              // PK
    private BigDecimal trustScore;         // 0–100
    private String riskLevel;              // LOW/MEDIUM/HIGH/CRITICAL
    private BigDecimal extremismScore;     // 0–1（选）
    private BigDecimal botLikelihood;      // 0–1（选）

    // TopN 观点摘要（不放全量证据）
    private List<StanceSummary> topStances;

    private Integer schemaVersion = 1;
    private LocalDateTime firstSeenAt;
    private LocalDateTime updatedAt = LocalDateTime.now();
}
