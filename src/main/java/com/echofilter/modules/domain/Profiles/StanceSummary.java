package com.echofilter.modules.domain.Profiles;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StanceSummary {
    private String issueCode;
    private String stance;                 // 与 AccountIssueStance 一致
    private BigDecimal intensity;
    private BigDecimal confidence;
    private LocalDateTime lastUpdated;
    private Integer evidenceCount;
}
