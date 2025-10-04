package com.echofilter.lowerLevel.infrastructure.modules.service;
import com.echofilter.lowerLevel.infrastructure.modules.dto.request.LlmPromptInput;
import com.echofilter.lowerLevel.infrastructure.modules.dto.response.AnalysisResponse;
import org.springframework.stereotype.Service;

/**
 *
 *
 *
 */
@Service
public interface CommentAnalysisService {
    AnalysisResponse getCommentResult(LlmPromptInput commentRequest);

}
