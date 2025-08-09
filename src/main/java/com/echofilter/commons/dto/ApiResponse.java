package com.echofilter.commons.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
public class ApiResponse<T> {
    private String code;                 // 业务码，如 OK
    private String message;              // 可读信息
    private T data;                      // 业务数据载体
    private String traceId;              // 从 MDC 注入
    private String path;                 // 请求路径
    private OffsetDateTime timestamp;    // 服务端时间
    private Map<String, Object> details; // 可选扩展
}