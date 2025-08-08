package com.echofilter.modules.ai.Impl;

import com.echofilter.commons.enums.ModelAPI;
import com.echofilter.modules.dto.request.CommentRequest;
import com.echofilter.modules.dto.response.AnalysisResponse;
import com.echofilter.modules.ai.LLMApi;
import org.springframework.stereotype.Component;

@Component
public class GeminiStrategy extends AbstractLLMStrategy {

    @Override
    protected String callModel(String prompt) {
        return null;
    }

    @Override
    public ModelAPI APIName() {
        return ModelAPI.GEMINI;
    }
}
