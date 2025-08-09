package com.echofilter.commons.clients.Impl;

import com.echofilter.commons.configs.OpenAiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component("openAiClient")
@RequiredArgsConstructor
public class OpenAiClientImpl extends BaseLLMClient {

    private final OpenAiProperties props;

    @Override
    protected String getApiUrl() {
        return props.getApiUrl();
    }

    @Override
    protected String getApiKey() {
        return props.getApiKey();
    }

    @Override
    protected Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> sys = Map.of("role", "system",
                "content", "You are a strict JSON generator. Respond with JSON only.");
        Map<String, Object> user = Map.of("role", "user", "content", prompt);

        return Map.of(
                "model", props.getModel(),
                "messages", List.of(sys, user),
                "temperature", props.getTemperature()
        );
    }

    @Override
    protected String extractTextFromResponse(String responseJson) throws IOException {
        return mapper.readTree(responseJson)
                .path("choices").path(0).path("message").path("content")
                .asText();
    }

}
