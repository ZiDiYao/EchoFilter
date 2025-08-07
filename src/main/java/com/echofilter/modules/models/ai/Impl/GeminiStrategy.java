package com.echofilter.modules.models.ai.Impl;

import com.echofilter.modules.commons.Enums.ModelAPI;
import com.echofilter.modules.dto.request.CommentRequest;
import com.echofilter.modules.dto.response.AnalysisResponse;
import com.echofilter.modules.models.ai.LLMApi;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
