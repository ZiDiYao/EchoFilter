package com.echofilter.lowerLevel.infrastructure.modules.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SocialMediaUser {

    private String id; // given by us
    private String platform;
    private String platformUserId; // given by the platform
    private LocalDateTime createdAt = LocalDateTime.now();

}
