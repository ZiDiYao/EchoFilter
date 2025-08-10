package com.echofilter.modules.ai.clients;

public interface LLMClient {
    String callLLM(String prompt, String model);
}