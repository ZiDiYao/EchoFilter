package com.echofilter.modules.ai.Impl;

import com.echofilter.modules.ai.clients.LLMClient;
import com.echofilter.commons.enums.ModelAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DeepSeekStrategy extends AbstractLLMStrategy {

    private final LLMClient client;

    @Autowired
    public DeepSeekStrategy(@Qualifier("deepSeekClient") LLMClient client) {
        this.client = client;
    }

    @Override
    protected String callModel(String prompt, String model) {
        return client.callLLM(prompt, model); // 新签名
    }

    @Override
    public ModelAPI APIName() { return ModelAPI.DEEPSEEK; }
}
