package com.echofilter.commons.clients.Impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DeepSeekClientImpl extends BaseLLMClient {

    private static final String API_KEY = "sk-xxx"; // 可从配置读取
    private static final String API_URL = "https://api.deepseek.com/chat/completions";

    @Override
    protected String getApiUrl() {
        return API_URL;
    }

    @Override
    protected String getApiKey() {
        return API_KEY;
    }

    @Override
    protected Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> system = Map.of(
                "role", "system",
                "content", "You are a helpful assistant."
        );

        Map<String, Object> user = Map.of(
                "role", "user",
                "content", prompt
        );

        return Map.of(
                "model", "deepseek-chat",
                "messages", List.of(system, user),
                "temperature", 0.2,
                "stream", false
        );
    }

    @Override
    protected String extractTextFromResponse(String responseJson) throws IOException {
        return mapper.readTree(responseJson)
                .get("choices").get(0)
                .get("message").get("content")
                .asText();
    }
}
