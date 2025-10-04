package com.echofilter.lowerLevel.infrastructure.modules.ai;

import com.echofilter.commons.enums.ModelAPI;
import com.echofilter.lowerLevel.infrastructure.modules.dto.request.LlmPromptInput;
import com.echofilter.lowerLevel.infrastructure.modules.dto.response.AnalysisResponse;

public interface LLMApi {

    ModelAPI APIName();
    AnalysisResponse handle(LlmPromptInput originalRequest);

}
