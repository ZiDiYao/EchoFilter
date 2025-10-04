package com.echofilter.lowerLevel.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
//import org.springframework.util.backoff.ExponentialBackOffWithMaxRetries;

@Configuration
@RequiredArgsConstructor
public class KafkaListenerConfig {

//    private final ConsumerFactory<String, String> consumerFactory;
//
//    /** 建议：和 topic 分区数匹配（示例：12 分区 → 这里也配 12） */
//    private static final int CONCURRENCY = 12;
//
//    /** 单条监听（poll 快返，业务丢到虚拟线程），成功后手动 ack */
//    @Bean(name = "kafkaContainerFactory")
//    public ConcurrentKafkaListenerContainerFactory<String, String> containerFactory(DefaultErrorHandler eh) {
//        var f = new ConcurrentKafkaListenerContainerFactory<String, String>();
//        f.setConsumerFactory(consumerFactory);
//        f.setConcurrency(CONCURRENCY);
//        f.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
//        f.getContainerProperties().setPollTimeout(1500L);   // 可选：1.5s poll 超时
//        f.setBatchListener(false);                          // 单条监听
//        f.setObservationEnabled(false);                     // 可选：若未用 Micrometer tracing
//        f.setCommonErrorHandler(eh);
//        return f;
//    }
//
//    /** 指数退避重试 + DLT：重试 5 次，1s 起、×2、最大 30s */
//    @Bean
//    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
//        // 进 *.DLT 主题，key/headers 保持
//        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
//
//        ExponentialBackOffWithMaxRetries backoff = new ExponentialBackOffWithMaxRetries(5);
//        backoff.setInitialInterval(1000);
//        backoff.setMultiplier(2.0);
//        backoff.setMaxInterval(30000);
//
//        DefaultErrorHandler h = new DefaultErrorHandler(recoverer, backoff);
//        // 例如：参数非法不重试，直接 DLT（按需增减）
//        h.addNotRetryableExceptions(IllegalArgumentException.class);
//        // h.setCommitRecovered(true); // 如果想 DLT 成功后自动提交 offset
//        return h;
//    }
}
