package com.echofilter.modules.controller;

import com.echofilter.commons.templates.PromptTemplates;
import com.echofilter.modules.dto.request.CommentRequest;
import com.echofilter.modules.dto.response.AnalysisResponse;
import com.echofilter.modules.ai.LLMApi;
import com.echofilter.modules.service.CommentAnalysisService;
import com.echofilter.modules.service.Impl.LLMApiFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/api/llm")
@RequiredArgsConstructor
public class CommentAnalysisController {

    private final CommentAnalysisService commentAnalysisService;
    private final LLMApiFactory llmApiFactory;
    private final PromptTemplates promptTemplates;

    @PostMapping("/analyze")
    public AnalysisResponse analyzeComment(@RequestBody CommentRequest request) {
        LLMApi api = llmApiFactory.getLLMApi(request.getLLMAPI());
        return api.handle(request);
    }
}
