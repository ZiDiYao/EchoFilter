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

    // this is not the id given by us. But, an id which is registered on social media
    @JsonProperty("userId")
    private Long PlatformUserId;
    @JsonProperty("LLMAPI")
    private String LLMAPI;

}
