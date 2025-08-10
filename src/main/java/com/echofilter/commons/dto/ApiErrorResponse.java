package com.echofilter.commons.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
public class ApiErrorResponse {
    private String code;
    private String message;
    private String traceId;
    private String path;
    private OffsetDateTime timestamp;
    private Map<String, Object> details;
}