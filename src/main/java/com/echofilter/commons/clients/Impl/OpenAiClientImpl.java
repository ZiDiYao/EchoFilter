package com.echofilter.commons.clients.Impl;

import com.echofilter.commons.clients.Impl.BaseLLMClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component("openAiClient")
public class OpenAiClientImpl extends BaseLLMClient {

    private static final String API_KEY = "sk-xxx"; // 可从配置中读取
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

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
        Map<String, Object> message = Map.of(
                "role", "user",
                "content", prompt
        );

        return Map.of(
                "model", "gpt-4",
                "messages", List.of(message),
                "temperature", 0.2
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
