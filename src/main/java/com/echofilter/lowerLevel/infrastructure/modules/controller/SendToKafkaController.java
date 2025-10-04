package com.echofilter.lowerLevel.infrastructure.modules.controller;

import com.echofilter.lowerLevel.infrastructure.messaging.kafka.producer.RequestProducer;
import com.echofilter.lowerLevel.infrastructure.modules.dto.request.BatchAnalyzeRequest;
import com.echofilter.lowerLevel.infrastructure.modules.dto.request.LlmPromptInput;
import com.echofilter.lowerLevel.infrastructure.modules.mappers.AnalyzeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analyze")
public class SendToKafkaController {

    private final RequestProducer producer;

    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batch(@RequestBody BatchAnalyzeRequest req) {
        String taskId = (req.taskId() == null || req.taskId().isBlank())
                ? "task-" + UUID.randomUUID()
                : req.taskId();
        String corrId = (req.correlationId() == null || req.correlationId().isBlank())
                ? "corr-" + UUID.randomUUID()
                : req.correlationId();

        String overrideLlm = req.llmApi();
        for (LlmPromptInput in : req.comments()) {
            var evt = AnalyzeMapper.toEvent(taskId, corrId, overrideLlm, in); // DTO->Event
            producer.send(evt); //  ef.requests.v1
        }
        return ResponseEntity.accepted().body(Map.of("taskId", taskId, "count", req.comments().size()));
    }
}
