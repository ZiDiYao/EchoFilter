package com.echofilter.modules.models.ai;

import com.echofilter.modules.commons.Enums.ModelAPI;
import com.echofilter.modules.dto.request.CommentRequest;
import com.echofilter.modules.dto.response.AnalysisResponse;
import org.json.JSONObject;

public interface LLMApi {

    ModelAPI APIName();
    AnalysisResponse handle(CommentRequest originalRequest);

}
