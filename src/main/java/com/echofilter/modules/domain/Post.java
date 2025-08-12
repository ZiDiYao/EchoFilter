package com.echofilter.modules.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Post {
    private String id;                 // 我们生成
    private String platform;
    private String externalPostId;     // 平台帖子ID（可空）
    private String authorAccountId;    // = SocialMediaAccount.id
    private String content;
    private String type;
    private String contentHash;        // 规范化后SHA-256
    private LocalDateTime createdAt = LocalDateTime.now();
}
