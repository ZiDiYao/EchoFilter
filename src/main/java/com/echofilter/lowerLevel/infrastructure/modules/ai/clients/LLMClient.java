package com.echofilter.lowerLevel.infrastructure.modules.ai.clients;

public interface LLMClient {
    String callLLM(String prompt, String model);
}