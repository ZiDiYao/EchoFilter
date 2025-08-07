package com.echofilter.modules.commons.Enums;

public enum ModelAPI {

    GPT_4,
    CLAUDE,
    GEMINI,
    DEEPSEEK;

    public static ModelAPI fromString(String name) {
        try {
            return ModelAPI.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("DO NOT SUPPORT THIS LLM RIGHT NOW: " + name);
        }
    }

}
