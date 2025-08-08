package com.echofilter.modules.dto.request;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class CommentRequest {
    @JsonProperty("platform")
    private String platform;

    @JsonProperty("context")
    private String context;

    @JsonProperty("content")
    private String content;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("LLMAPI")
    private String LLMAPI;

}
