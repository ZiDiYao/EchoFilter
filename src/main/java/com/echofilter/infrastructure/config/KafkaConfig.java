package com.echofilter.infrastructure.config;

import com.echofilter.infrastructure.messaging.kafka.topics.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * 自动注册 Kafka 主题：
 * - ef.requests.v1        （delete 清理，7 天）
 * - ef.results.v1         （compact+delete，7 天兜底）
 * - ef.requests.DLT.v1    （DLT，30 天）
 * - ef.results.DLT.v1     （DLT，30 天）
 *
 * 说明：
 * 1) 需要在 application.yml 里配置 spring.kafka.bootstrap-servers
 * 2) 只要工程里存在 NewTopic Bean，Spring 会在启动时用 KafkaAdmin 尝试创建缺失的 topic
 */
@Configuration
public class KafkaConfig {

    /** 可选：按环境前缀隔离（例如 dev./prod.），不需要就留空 */
    @Value("${ef.kafka.prefix:}")
    private String envPrefix;

    /** 分区/副本/保留时间（根据环境调） */
    private static final int   PARTITIONS = 12;   // 开发可以 3~6；生产按并发调
    private static final short REPLICAS   = 1;    // 开发 1；生产建议 >= 3
    private static final String RET_7D    = String.valueOf(7L  * 24 * 60 * 60 * 1000);
    private static final String RET_30D   = String.valueOf(30L * 24 * 60 * 60 * 1000);

    private String name(String base) {
        return (envPrefix == null || envPrefix.isBlank()) ? base : envPrefix + "." + base;
    }

    /** 请求主题：只做时间保留 */
    @Bean
    public NewTopic requestsTopic() {
        return TopicBuilder.name(name(KafkaTopics.REQ))
                .partitions(PARTITIONS)
                .replicas(REPLICAS)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .config(TopicConfig.RETENTION_MS_CONFIG, RET_7D)
                .build();
    }

    /** 结果主题：compact + delete（既保留每个 key 的最新值，也保留 7 天） */
    @Bean
    public NewTopic resultsTopic() {
        return TopicBuilder.name(name(KafkaTopics.RES))
                .partitions(PARTITIONS)
                .replicas(REPLICAS)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG,
                        TopicConfig.CLEANUP_POLICY_COMPACT + "," + TopicConfig.CLEANUP_POLICY_DELETE)
                .config(TopicConfig.MIN_CLEANABLE_DIRTY_RATIO_CONFIG, "0.5")
                .config(TopicConfig.RETENTION_MS_CONFIG, RET_7D)
                .build();
    }

    /** 请求侧 DLT：失败多次进入死信，保留更久用于排障 */
    @Bean
    public NewTopic requestsDltTopic() {
        return TopicBuilder.name(name(KafkaTopics.REQ_DLT))
                .partitions(PARTITIONS)
                .replicas(REPLICAS)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .config(TopicConfig.RETENTION_MS_CONFIG, RET_30D)
                .build();
    }

    /** 结果侧 DLT（为对称性保留；通常很少用） */
    @Bean
    public NewTopic resultsDltTopic() {
        return TopicBuilder.name(name(KafkaTopics.RES_DLT))
                .partitions(PARTITIONS)
                .replicas(REPLICAS)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .config(TopicConfig.RETENTION_MS_CONFIG, RET_30D)
                .build();
    }
}
