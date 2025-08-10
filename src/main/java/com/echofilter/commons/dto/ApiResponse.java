package com.echofilter.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String errorCode;
    private String errorMessage;

    public static <T> ApiResponse<T> ok(T data){ return new ApiResponse<>(true, data, null, null); }
    public static <T> ApiResponse<T> fail(String code, String msg){ return new ApiResponse<>(false, null, code, msg); }
}
