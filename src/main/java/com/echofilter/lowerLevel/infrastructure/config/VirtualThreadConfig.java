package com.echofilter.lowerLevel.infrastructure.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
public class VirtualThreadConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService virtualThreadExecutor() {
        // 命名一下线程，便于日志/排障
        ThreadFactory tf = Thread.ofVirtual().name("vf-").factory();
        return Executors.newThreadPerTaskExecutor(tf); // = newVirtualThreadPerTaskExecutor() 的可命名版本
    }
}
