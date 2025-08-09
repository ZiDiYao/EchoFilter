package com.echofilter.commons.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
public class ApiErrorResponse {
    private String code;                 // 错误码，如 LLM_RATE_LIMITED
    private String message;              // 错误描述
    private String traceId;
    private String path;
    private OffsetDateTime timestamp;
    private Map<String, Object> details; // 校验错误列表等
}