package com.echofilter.modules.controller;

import com.echofilter.commons.templates.PromptTemplates;
import com.echofilter.modules.dto.request.CommentRequest;
import com.echofilter.modules.dto.response.AnalysisResponse;
import com.echofilter.modules.service.CommentAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/api/llm")
@RequiredArgsConstructor
public class CommentAnalysisController {

    private final CommentAnalysisService commentAnalysisService;
    private final PromptTemplates promptTemplates;

    /**
     *
     * @param request
     * @return
     */

    @PostMapping("/analyze")
    public AnalysisResponse analyzeComment(@RequestBody CommentRequest request) {
        return commentAnalysisService.getCommentResult(request);
    }

}
