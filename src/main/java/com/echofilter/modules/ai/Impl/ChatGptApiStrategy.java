package com.echofilter.modules.ai.Impl;

import com.echofilter.commons.clients.LLMClient;
import com.echofilter.commons.enums.ModelAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ChatGptApiStrategy extends AbstractLLMStrategy {

    private final LLMClient client;

    @Autowired
    public ChatGptApiStrategy(@Qualifier("openAiClient") LLMClient client) {
        this.client = client;
    }

    @Override
    protected String callModel(String prompt) { return client.callLLM(prompt); }

    @Override
    public ModelAPI APIName() { return ModelAPI.GPT_4; }
}
