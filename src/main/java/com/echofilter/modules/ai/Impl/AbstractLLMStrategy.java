package com.echofilter.modules.ai.Impl;

import com.echofilter.modules.ai.configs.LlmProperties;
import com.echofilter.commons.templates.PromptTemplates;
import com.echofilter.commons.utils.json.JsonHandler;
import com.echofilter.modules.ai.LLMApi;
import com.echofilter.modules.dto.request.LlmPromptInput;
import com.echofilter.modules.dto.response.AnalysisResponse;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractLLMStrategy implements LLMApi {

    @Autowired protected PromptTemplates promptTemplates;
    @Autowired protected LlmProperties llmProperties;
    @Autowired protected JsonHandler json;

    @Override
    public AnalysisResponse handle(LlmPromptInput request) {
        String prompt = buildPrompt(request);
        String raw = callModelWithResolvedModel(prompt);

        // 1) 提取最外层 JSON 串
        String jsonOnly = json.extractJsonSafely(raw);

        // 2) 直接反序列化为对象（省去手工 set）
        AnalysisResponse r = json.fromJson(jsonOnly, AnalysisResponse.class);

        // 3) 后置收敛（数值边界、缺省类型）
        normalize(r);
        System.out.println("handle:" + r.getFacts());
        return r;
    }

    protected String buildPrompt(LlmPromptInput request) {
        return promptTemplates.buildCommentAnalysis(
                request.platform(),
                request.language(),
                request.content(),
                request.context()
        );
    }


    // Call Models
    protected String callModelWithResolvedModel(String prompt) {
        String realModel = llmProperties.resolveRealModel(APIName().name());
        return callModel(prompt, realModel);
    }

    protected abstract String callModel(String prompt, String model);

    /** 统一做些兜底和边界保护 */
    protected void normalize(AnalysisResponse r) {
        System.out.println("normalize:" + r.getFacts());
        if (r.getType() == null || r.getType().isBlank()) {
            r.setType("opinion");
        } else {
            String t = r.getType().toLowerCase();
            if (!("opinion".equals(t) || "fact".equals(t) || "misinformation".equals(t))) {
                r.setType("opinion");
            } else {
                r.setType(t);
            }
        }
        r.setConfidence(bound01(r.getConfidence()));
        r.setTrustScore(bound01(r.getTrustScore()));
        if (r.getFacts() == null) {
            r.setFacts(java.util.Collections.emptyList());
        }
    }

    private static double bound01(double v){ return Math.max(0.0, Math.min(1.0, v)); }
}
