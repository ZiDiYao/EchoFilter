package com.echofilter.commons.clients.Impl;

import com.echofilter.commons.clients.LLMClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public abstract class BaseLLMClient implements LLMClient {
    protected static final ObjectMapper mapper = new ObjectMapper();
    protected final OkHttpClient client;

    protected BaseLLMClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

    @Override
    public String callLLM(String prompt) {
        try {
            Request request = new Request.Builder()
                    .url(getApiUrl())
                    .addHeader("Authorization", "Bearer " + getApiKey())
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(
                            mapper.writeValueAsString(buildRequestBody(prompt)),
                            MediaType.parse("application/json")
                    ))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("LLM API Call Failed: " + response);
                }

                assert response.body() != null;
                String body = response.body().string();
                return extractTextFromResponse(body);
            }
        } catch (Exception e) {
            throw new RuntimeException("LLM API Calling ERROR", e);
        }
    }

    protected abstract String getApiUrl();

    protected abstract String getApiKey();

    protected abstract Map<String, Object> buildRequestBody(String prompt);

    protected abstract String extractTextFromResponse(String responseJson) throws IOException;
}
