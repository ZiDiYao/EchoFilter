package com.echofilter.lowerLevel.infrastructure.modules.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SocialMediaAccount {
    private String id;                 // 我们生成（ULID/UUIDv7/UUID）
    private String platform;           // "REDDIT" / "X" / "YOUTUBE" ...
    private String platformUserId;     // 平台给的用户ID（去重用）
    private LocalDateTime createdAt = LocalDateTime.now();
}