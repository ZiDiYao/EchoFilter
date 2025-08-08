package com.echofilter.commons.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OpenAiClient {

    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String callGpt(String prompt) {
        OkHttpClient client = new OkHttpClient();

        try {
            Map<String, Object> message = Map.of(
                    "role", "user",
                    "content", prompt
            );

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4",
                    "messages", List.of(message),
                    "temperature", 0.2
            );

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(
                            mapper.writeValueAsString(requestBody),
                            MediaType.parse("application/json")
                    ))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("GPT Calling Fails: " + response);
                }

                assert response.body() != null;
                String body = response.body().string();
                return mapper.readTree(body)
                        .get("choices").get(0)
                        .get("message").get("content")
                        .asText();
            }
        } catch (Exception e) {
            throw new RuntimeException("GPT API Calling ERROR", e);
        }
    }
}
