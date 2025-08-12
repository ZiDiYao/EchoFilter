package com.echofilter.modules.domain.Profiles;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountIssueStance {
    private String accountId;              // PK1
    private String issueCode;              // PK2
    private String stance;                 // SUPPORT/OPPOSE/MIXED/NEUTRAL/UNKNOWN
    private BigDecimal intensity;          // 0~1（立场强度）
    private BigDecimal confidence;         // 0~1（我们对该判断的置信度）
    private String source;                 // SELF_DECLARED/ML/ADMIN
    private LocalDateTime asOf;            // 最近一次结论时间
    private Integer evidenceCount;         // 证据条数（冗余，便于排序）
    private String requestFingerprint;     // 模型/模板/阈值指纹，溯源
}
