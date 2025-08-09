package com.echofilter.modules.ai.Impl;

import com.echofilter.modules.ai.LLMApi;
import com.echofilter.modules.dto.request.CommentRequest;
import com.echofilter.modules.dto.response.AnalysisResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLLMStrategy implements LLMApi {

    @Value("${llm.prompt.template}")
    protected String promptTemplate;

    @Override
    public AnalysisResponse handle(CommentRequest request) {
        String prompt = buildPrompt(request);
        String raw = callModel(prompt);
        String jsonOnly = extractJsonSafely(raw);   // <-- guardrail
        JSONObject obj = new JSONObject(jsonOnly);
        return parseAnalysisResponse(obj);
    }

    protected String buildPrompt(CommentRequest request) {
        // Use platform + content; if you also want context, add it to the template and format here.
        return promptTemplate.formatted(request.getPlatform(), request.getContent());
    }

    protected AnalysisResponse parseAnalysisResponse(JSONObject json) {
        AnalysisResponse r = new AnalysisResponse();
        r.setType(json.optString("type", "opinion"));
        r.setConfidence(bound01(json.optDouble("confidence", 0.5)));
        r.setTrustScore(bound01(json.optDouble("trustScore", 0.5)));

        List<String> facts = new ArrayList<>();
        JSONArray arr = json.optJSONArray("facts");
        if (arr != null) for (int i = 0; i < arr.length(); i++) facts.add(arr.optString(i));
        r.setFacts(facts);
        return r;
    }

    private static double bound01(double v){ return Math.max(0.0, Math.min(1.0, v)); }

    /** handle code fences or extra prose from models */
    private static String extractJsonSafely(String text) {
        if (text == null) throw new IllegalArgumentException("LLM empty response");
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) return text.substring(start, end + 1);
        // last resort: return a minimal fallback to avoid 500s
        return "{\"type\":\"opinion\",\"confidence\":0.0,\"facts\":[],\"trustScore\":0.0}";
    }

    protected abstract String callModel(String prompt);
}
