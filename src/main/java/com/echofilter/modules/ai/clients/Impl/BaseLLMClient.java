package com.echofilter.modules.ai.clients.Impl;

import com.echofilter.modules.ai.clients.LLMClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class BaseLLMClient implements LLMClient {
    protected static final ObjectMapper mapper = new ObjectMapper();
    protected final OkHttpClient client;

    protected BaseLLMClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .callTimeout(90, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    @Override
    public String callLLM(String prompt, String model) {
        try {
            Request request = new Request.Builder()
                    .url(getApiUrl())
                    .addHeader("Authorization", "Bearer " + getApiKey())
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(
                            mapper.writeValueAsString(buildRequestBody(prompt, model)),
                            MediaType.parse("application/json")))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String body = response.body() != null ? response.body().string() : "";
                    throw new IOException("LLM API call failed: " + response.code() + " " + response.message() + " body=" + body);
                }
                String resp = response.body() != null ? response.body().string() : "";
                return extractTextFromResponse(resp);
            }
        } catch (Exception e) {
            throw new RuntimeException("LLM API Calling ERROR: " + e.getMessage(), e);
        }
    }

    /** 子类提供：URL、Key、JSON 请求体（含 model）、以及如何从响应里取文本 */
    protected abstract String getApiUrl();
    protected abstract String getApiKey();
    protected abstract Map<String, Object> buildRequestBody(String prompt, String model);
    protected abstract String extractTextFromResponse(String responseJson) throws IOException;
}
