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
    protected String promptTemplate; // 从配置读取 JSON 结构描述和提示

    @Override
    public AnalysisResponse handle(CommentRequest request) {
        String prompt = buildPrompt(request);
        String resultText = callModel(prompt);
        JSONObject responseJson = new JSONObject(resultText);
        return parseAnalysisResponse(responseJson);
    }

    protected String buildPrompt(CommentRequest request) {
        return promptTemplate.formatted(request.getPlatform(), request.getContent());
    }

    protected AnalysisResponse parseAnalysisResponse(JSONObject json) {
        AnalysisResponse response = new AnalysisResponse();
        response.setType(json.optString("type"));
        response.setConfidence(json.optDouble("confidence"));
        response.setTrustScore(json.optDouble("trustScore"));

        List<String> facts = new ArrayList<>();
        JSONArray array = json.optJSONArray("facts");
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                facts.add(array.getString(i));
            }
        }
        response.setFacts(facts);
        return response;
    }

    // 留给子类实现：具体调用哪个模型（OpenAI / DeepSeek 等）
    protected abstract String callModel(String prompt);
}
