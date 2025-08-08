package com.echofilter.modules.service;

import com.echofilter.modules.dto.request.CommentRequest;
import com.echofilter.modules.dto.response.AnalysisResponse;
import org.springframework.stereotype.Service;

/**
 *
 *
 *
 */
@Service
public interface CommentAnalysisService {
    AnalysisResponse getCommentResult(CommentRequest commentRequest);

}
