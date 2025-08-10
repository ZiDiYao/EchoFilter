package com.echofilter.modules.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Post {
    private Long postId;
    private String platform;
    private SocialMediaUser author;
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();


}
