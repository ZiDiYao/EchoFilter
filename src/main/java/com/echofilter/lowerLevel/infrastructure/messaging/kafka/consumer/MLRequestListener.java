package com.echofilter.lowerLevel.infrastructure.messaging.kafka.consumer;


import com.echofilter.lowerLevel.infrastructure.modules.factories.LLMApiFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

@Component
@RequiredArgsConstructor
public class MLRequestListener {

    private final LLMApiFactory factory;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ExecutorService virtualThreadExecutor;
    private final Semaphore llmSlots;

    private static final String RESULT_TOPIC = "ef.results.v1";

    @KafkaListener(topics = "ef.requests.v1", groupId = "echofilter-group")
    public void onMessage(  String payload,
                            @Header(name = "x-correlation-id", required = false) String corrHeader,
                            @Header(name = "x-schema", required = false) String schemaHeader){

        // 监听器线程快速返回，把重活交给虚拟线程

    }


}
