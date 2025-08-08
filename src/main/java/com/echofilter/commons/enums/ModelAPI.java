package com.echofilter.commons.enums;

import java.util.Optional;

public enum ModelAPI {

    GPT_4,
    CLAUDE,
    GEMINI,
    DEEPSEEK;

    public static ModelAPI fromString(String name) {
        return Optional.ofNullable(name)
                .map(String::toUpperCase)
                .map(value -> {
                    try {
                        return ModelAPI.valueOf(value);
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("DO NOT SUPPORT THIS LLM RIGHT NOW: " + value);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Model name must not be null"));
    }

}
