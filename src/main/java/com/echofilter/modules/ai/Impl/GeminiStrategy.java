package com.echofilter.modules.ai.Impl;

import com.echofilter.commons.enums.ModelAPI;
import com.echofilter.modules.dto.request.CommentRequest;
import com.echofilter.modules.dto.response.AnalysisResponse;
import com.echofilter.modules.ai.LLMApi;
import org.springframework.stereotype.Component;

@Component
public class GeminiStrategy implements LLMApi {
    @Override
    public ModelAPI APIName() {
        return null;
    }

    @Override
    public AnalysisResponse handle(CommentRequest originalRequest) {
        return null;

    }
}
