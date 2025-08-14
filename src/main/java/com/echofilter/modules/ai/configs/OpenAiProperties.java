package com.echofilter.modules.ai.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {
    private String apiUrl;
    private String apiKey;
    private String model;
    private double temperature = 0.0;
}