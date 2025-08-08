package com.echofilter.modules.ai.Impl;
import com.echofilter.commons.clients.LLMClient;
import com.echofilter.commons.enums.ModelAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *  Strategy pattern should be applied
 *
 */

@Component
@RequiredArgsConstructor
public class ChatGptApiStrategy extends AbstractLLMStrategy {

    @Qualifier("openAiClient")
    private final LLMClient client;
    @Override
    protected String callModel(String prompt) {
        return client.callLLM(prompt);
    }

    @Override
    public ModelAPI APIName() {
        return ModelAPI.GPT_4;
    }


}
