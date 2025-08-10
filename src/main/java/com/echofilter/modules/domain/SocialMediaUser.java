package com.echofilter.modules.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SocialMediaUser {

    private Long id;
    private String platform;
    private UserProfile userProfile;
    private String platformUserId;
    private String username;
    private LocalDateTime createdAt = LocalDateTime.now();


}
