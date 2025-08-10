package com.echofilter.modules.ai.clients.Impl;

import com.echofilter.modules.ai.clients.Impl.BaseLLMClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component("deepSeekClient")
public class DeepSeekClientImpl extends BaseLLMClient {

    @Value("${deepseek.api-url}")
    private String apiUrl;

    @Value("${deepseek.api-key:}")
    private String apiKey;

    @Override protected String getApiUrl() { return apiUrl; }
    @Override protected String getApiKey() { return apiKey; }

    @Override
    protected Map<String, Object> buildRequestBody(String prompt, String model) {
        // DeepSeek 大多兼容 OpenAI Chat Completions 格式；如果报错，再去掉不支持的字段
        return Map.of(
                "model", model,
                "temperature", 0,
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
            throw new IOException("DeepSeek response missing content: " + responseJson);
        }
        return content.asText();
    }
}
