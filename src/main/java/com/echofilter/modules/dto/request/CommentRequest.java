package com.echofilter.modules.dto.request;

import lombok.Data;

@Data
public class CommentRequest {
    private String platform;
    private String content;
    private Long userId;
    private String LLMAPI; // internal decision about which LLM we are going to use
}
