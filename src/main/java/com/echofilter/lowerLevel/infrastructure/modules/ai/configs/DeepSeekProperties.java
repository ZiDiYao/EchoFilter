package com.echofilter.lowerLevel.infrastructure.modules.ai.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekProperties {
    private String apiUrl;
    private String apiKey;
    private String model;
    private double temperature = 0.0;
}