package com.echofilter.lowerLevel.infrastructure.modules.controller;

import com.echofilter.crosscutting.AOP.annotation.ApiLog;
import com.echofilter.lowerLevel.infrastructure.modules.dto.request.LlmPromptInput;
import com.echofilter.lowerLevel.infrastructure.modules.dto.response.AnalysisResponse;
import com.echofilter.lowerLevel.infrastructure.modules.service.CommentAnalysisService;
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

    /**
     *
     * @param request
     * @return
     */

    @PostMapping("/analyze")
    @ApiLog(sample = 1.0, argsMax = 2048, resultMax = 2048, logHeaders = false)
    public AnalysisResponse analyzeComment(@RequestBody LlmPromptInput request) {
        System.out.println("analyzeComment controller has been called");
        return commentAnalysisService.getCommentResult(request);
    }

}
