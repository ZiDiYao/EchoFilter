package com.echofilter.modules.controller;

import com.echofilter.modules.dto.request.LlmPromptInput;
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

    /**
     *
     * @param request
     * @return
     */

    @PostMapping("/analyze")
    public AnalysisResponse analyzeComment(@RequestBody LlmPromptInput request) {
        System.out.println("analyzeComment controller has been called");
        return commentAnalysisService.getCommentResult(request);
    }

}
