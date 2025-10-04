package com.echofilter.lowerLevel.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Semaphore;

@Configuration
public class ConcurrencyGuards {

    @Bean
    public Semaphore llmSlots() {
        return new Semaphore(200); // maximum 200
    }
}
