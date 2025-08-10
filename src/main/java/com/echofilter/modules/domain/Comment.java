package com.echofilter.modules.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {

    private Long commentId;
    private Post post;
    private SocialMediaUser author;
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();

}
