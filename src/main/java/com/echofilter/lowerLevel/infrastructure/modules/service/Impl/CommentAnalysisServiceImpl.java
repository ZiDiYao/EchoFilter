package com.echofilter.lowerLevel.infrastructure.modules.service.Impl;

import com.echofilter.lowerLevel.infrastructure.modules.factories.LLMApiFactory;
import com.echofilter.lowerLevel.infrastructure.modules.service.CommentAnalysisService;
import com.echofilter.lowerLevel.infrastructure.modules.ai.LLMApi;
import com.echofilter.lowerLevel.infrastructure.modules.dto.request.LlmPromptInput;
import com.echofilter.lowerLevel.infrastructure.modules.dto.response.AnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentAnalysisServiceImpl implements CommentAnalysisService {
    // choose which model we are going to use first.
    private final LLMApiFactory llmApiFactoryImpl;
    // use the model, then call the API
    @Override
//    @Cacheable(
//            cacheNames = "analysis",
//            key =
//                    "T(com.echofilter.commons.utils.text.Hash256).sha256Hex(" +
//                            "  T(com.echofilter.commons.utils.text.TextNormalizer).normalize(" +
//                            "     #p0.content() + '|' + (#p0.context() == null ? '' : #p0.context()) + '|' + #p0.LlmApi()" +
//                            "  )" +
//                            ")"
//    )
    public AnalysisResponse getCommentResult(LlmPromptInput commentRequest) {
        System.out.println("Request LLMAPI: " + commentRequest.LlmApi());
        LLMApi api = llmApiFactoryImpl.getLLMApi(commentRequest.LlmApi());
        System.out.println("After Factory: " + api.APIName());
        return api.handle(commentRequest);
    }

}