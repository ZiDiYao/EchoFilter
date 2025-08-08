package com.echofilter.modules.factories;

import com.echofilter.commons.enums.ModelAPI;
import com.echofilter.modules.ai.LLMApi;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * be responsible for choosing which LLM we are going to use
 */
import java.util.HashMap;
import java.util.Map;

@Component
public class LLMApiFactory {

    private final Map<ModelAPI, LLMApi> strategyMap = new HashMap<>();

    public LLMApiFactory(List<LLMApi> strategies) {
        for (LLMApi api : strategies) {
            System.out.println("Registered LLM Strategy: " + api.APIName());
            strategyMap.put(api.APIName(), api);
        }
    }

    public LLMApi getLLMApi(String name) {
        ModelAPI model = ModelAPI.fromString(name);
        LLMApi api = strategyMap.get(model);
        if (api == null) {
            throw new RuntimeException("Did not find corresponding LLM Model: " + name);
        }
        return api;
    }
}
