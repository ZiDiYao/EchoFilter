package com.echofilter.modules.dto.request;

import lombok.Data;

@Data
public class CommentRequest {
    private String platform;
    private String content;
    private Long userId;
}
