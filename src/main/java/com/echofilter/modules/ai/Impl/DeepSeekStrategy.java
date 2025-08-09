package com.echofilter.modules.ai.Impl;

import com.echofilter.commons.clients.LLMClient;
import com.echofilter.commons.enums.ModelAPI;
import com.echofilter.modules.ai.LLMApi;
import com.echofilter.modules.dto.request.CommentRequest;
import com.echofilter.modules.dto.response.AnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

// DeepSeekStrategy.java
@Component
public class DeepSeekStrategy extends AbstractLLMStrategy {

    private final LLMClient client;

    @Autowired
    public DeepSeekStrategy(@Qualifier("deepSeekClient") LLMClient client) {
        this.client = client;
    }

    @Override protected String callModel(String prompt) { return client.callLLM(prompt); }

    @Override public ModelAPI APIName() { return ModelAPI.DEEPSEEK; }
}
