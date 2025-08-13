package com.echofilter.modules.service.Impl;

import com.echofilter.modules.ai.LLMApi;
import com.echofilter.modules.dto.request.CommentRequest;
import com.echofilter.modules.dto.response.AnalysisResponse;
import com.echofilter.modules.factories.LLMApiFactory;
import com.echofilter.modules.service.CommentAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentAnalysisServiceImpl implements CommentAnalysisService {
    // choose which model we are going to use first.
    private final LLMApiFactory llmApiFactoryImpl;
    // use the model, then call the API
    @Override
    public AnalysisResponse getCommentResult(CommentRequest commentRequest) {
        System.out.println("Request LLMAPI: " + commentRequest.getLlmApi());
        LLMApi api = llmApiFactoryImpl.getLLMApi(commentRequest.getLlmApi());
        System.out.println("After Factory: " + api.APIName());
        return api.handle(commentRequest);
    }

}
