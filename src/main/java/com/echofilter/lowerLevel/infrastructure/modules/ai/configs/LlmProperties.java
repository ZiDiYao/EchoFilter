package com.echofilter.lowerLevel.infrastructure.modules.ai.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "llm")
public class LlmProperties {
    private Prompt prompt = new Prompt();
    private Map<String, String> models; // key: enum name (e.g., GPT_4), value: real model string

    public static class Prompt {
        private String template;
        public String getTemplate() { return template; }
        public void setTemplate(String template) { this.template = template; }
    }


    public Prompt getPrompt() { return prompt; }
    public void setPrompt(Prompt prompt) { this.prompt = prompt; }

    public Map<String, String> getModels() { return models; }
    public void setModels(Map<String, String> models) { this.models = models; }

    public String resolveRealModel(String enumName) {
        if (models == null || !models.containsKey(enumName)) {
            throw new IllegalArgumentException("Model mapping not found for key: " + enumName);
        }
        return models.get(enumName);
    }


}
