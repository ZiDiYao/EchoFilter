package com.echofilter.modules.models.ai.Impl;

import com.echofilter.modules.commons.Enums.ModelAPI;
import com.echofilter.modules.commons.clients.OpenAiClient;
import com.echofilter.modules.dto.request.CommentRequest;
import com.echofilter.modules.dto.response.AnalysisResponse;
import com.echofilter.modules.models.ai.LLMApi;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 *  Strategy pattern should be applied
 *
 */

@Component
public class ChatGptApiStrategy implements LLMApi {

    @Override
    public ModelAPI APIName() {
        return ModelAPI.GPT_4;
    }

    // json is for defining the return type
    // commentRequest is the basic information of the comment

        @Override
        public AnalysisResponse handle(CommentRequest request) {
            String prompt = buildPrompt(request);

            // 1. Call GPT with the constructed prompt
            String resultText = OpenAiClient.callGpt(prompt);

            // 2. Parse the returned text into JSON
            JSONObject responseJson = new JSONObject(resultText);

            // 3. Convert to DTO object and process
            AnalysisResponse response = parseAnalysisResponse(responseJson);

            System.out.println("Processing completed: " + response);
            return response;

            // TODO: Save to database, publish to message queue, or return response as needed
        }

        /**
         * Builds a prompt for ChatGPT based on the input comment
         */
        private String buildPrompt(CommentRequest request) {
            return """
                    Please perform an objective factual analysis of the following user comment and return the result strictly in JSON format as shown below:
                    
                    {
                      "type": "opinion|fact|misinformation",
                      "confidence": number between 0 and 1,
                      "facts": ["fact 1", "fact 2"],
                      "trustScore": number between 0 and 1
                    }
                    
                    **Explanation of fields:**
                    - "type": Classify the comment as either an opinion, factual statement, or misinformation.
                    - "confidence": Your confidence (0 to 1) in this classification.
                    - "facts": A list of concrete facts (from general knowledge or consensus) that support your classification, if applicable.
                    - "trustScore": Your evaluation (0 to 1) of how trustworthy the comment is, based on known facts and context.
                    
                    **Requirements:**
                    - Be strictly objective.
                    - If the comment contains misinformation, return factual corrections in the "facts" array.
                    - DO NOT include any explanation outside of the JSON object.
                    - DO NOT return markdown or code block formatting.
                    
                    Platform: %s
                    Comment: %s
                    """.formatted(request.getPlatform(), request.getContent());
        }


        /**
         * Parses the GPT response JSON into a strongly typed DTO
         */
        private AnalysisResponse parseAnalysisResponse(JSONObject json) {
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

}
