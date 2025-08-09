package com.echofilter.commons.clients.Impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component("deepSeekClient")
public class DeepSeekClientImpl extends BaseLLMClient {

    @Value("${deepseek.api-url}")
    private String apiUrl;

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.model}")
    private String model;

    @Override protected String getApiUrl() { return apiUrl; }
    @Override protected String getApiKey() { return apiKey; }

    @Override
    protected Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> sys = Map.of("role", "system",
                "content", "You are a strict JSON generator. Respond with JSON only.");
        Map<String, Object> user = Map.of("role", "user", "content", prompt);

        return Map.of(
                "model", model,
                "messages", List.of(sys, user),
                "temperature", 0.0,
                "stream", false
        );
    }

    @Override
    protected String extractTextFromResponse(String responseJson) throws IOException {
        return mapper.readTree(responseJson)
                .path("choices").path(0).path("message").path("content").asText();
    }
}
