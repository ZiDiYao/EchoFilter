package com.echofilter.modules.models.ai;

import com.echofilter.modules.dto.request.CommentRequest;
import org.json.JSONObject;

public interface LLMApi {
    void handle(JSONObject json, CommentRequest originalRequest);
    // json is the structure of the output given by LLM such as Chatgpt
}
