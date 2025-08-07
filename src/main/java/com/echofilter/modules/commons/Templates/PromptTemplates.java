package com.echofilter.modules.commons.Templates;

import org.springframework.stereotype.Component;

@Component
public class PromptTemplates {

    public static final String COMMENT_ANALYSIS_TEMPLATE = """
Please perform an objective factual analysis of the following user comment and return the result strictly in JSON format as shown below:

{
  "type": "opinion|fact|misinformation",
  "confidence": number between 0 and 1,
  "facts": ["fact 1", "fact 2"],
  "trustScore": number between 0 and 1
}

Explanation of fields:
- "type": Classify the comment as either an opinion, factual statement, or misinformation.
- "confidence": Your confidence (0 to 1) in this classification.
- "facts": A list of concrete facts (from general knowledge or consensus) that support your classification, if applicable.
- "trustScore": Your evaluation (0 to 1) of how trustworthy the comment is, based on known facts and context.

Requirements:
- Be strictly objective.
- If the comment contains misinformation, return factual corrections in the "facts" array.
- DO NOT include any explanation outside of the JSON object.
- DO NOT return markdown or code block formatting.

Platform: %s
Comment: %s
""";
}