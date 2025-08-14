package com.echofilter.modules.ai;

import com.echofilter.commons.enums.ModelAPI;
import com.echofilter.modules.dto.request.LlmPromptInput;
import com.echofilter.modules.dto.response.AnalysisResponse;

public interface LLMApi {

    ModelAPI APIName();
    AnalysisResponse handle(LlmPromptInput originalRequest);

}
