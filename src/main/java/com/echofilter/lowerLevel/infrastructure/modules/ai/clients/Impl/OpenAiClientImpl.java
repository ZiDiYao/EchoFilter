package com.echofilter.lowerLevel.infrastructure.modules.ai.clients.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component("openAiClient")
public class OpenAiClientImpl extends BaseLLMClient {

    @Value("${openai.api-url}")
    private String apiUrl;

    @Value("${openai.api-key}")
    private String apiKey;

    @Override
    protected String getApiUrl() { return apiUrl; }

    @Override
    protected String getApiKey() { return apiKey; }

    @Override
    protected Map<String, Object> buildRequestBody(String prompt, String model) {
        // 支持 JSON only 的新版可加 response_format；不支持就去掉该字段
        return Map.of(
                "model", model,
                "temperature", 0,
                "response_format", Map.of("type", "json_object"),
                "messages", new Object[] {
                        Map.of("role", "system", "content",
                                "You are a precise JSON-only analyzer. Respond with a single JSON object and nothing else."),
                        Map.of("role", "user", "content", prompt)
                }
        );
    }

    @Override
    protected String extractTextFromResponse(String responseJson) throws IOException {
        JsonNode root = mapper.readTree(responseJson);
        JsonNode content = root.path("choices").path(0).path("message").path("content");
        if (content.isMissingNode() || content.asText().isBlank()) {
            throw new IOException("OpenAI response missing content: " + responseJson);
        }
        return content.asText();
    }
}
