package com.echofilter.modules.models.ai.Impl;

import com.echofilter.modules.dto.request.CommentRequest;
import com.echofilter.modules.models.ai.LLMApi;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 *  Strategy pattern should be applied
 *
 */

@Service
public class ChatGptApiStrategy implements LLMApi {

    @Override
    public void handle(JSONObject json, CommentRequest originalRequest) {

    }
}
