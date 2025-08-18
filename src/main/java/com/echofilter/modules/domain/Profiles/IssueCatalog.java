package com.echofilter.modules.domain.Profiles;

import lombok.Data;

@Data
public class IssueCatalog {
    private String issueCode;     // PK, e.g. "health.tcm"
    private String category;      // "health"
    private String name;          // 展示名
    private String description;
    private String synonymsJson;  // 触发词/同义词
    private Integer version = 1;
    private Boolean enabled = true;
}
