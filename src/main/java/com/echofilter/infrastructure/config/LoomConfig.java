package com.echofilter.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Configuration
public class LoomConfig {

    /** ML 并发上限（按你下游能力调） */
    @Bean
    public Semaphore mlGate() {
        return new Semaphore(200);
    }

    /** 每任务一虚拟线程（JDK 21 原生） */
    @Bean(destroyMethod = "close")
    public ExecutorService vThreads() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}