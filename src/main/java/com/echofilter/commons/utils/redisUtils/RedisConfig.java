package com.echofilter.commons.utils.redisUtils;

import com.echofilter.modules.dto.response.AnalysisResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;
import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory, ObjectMapper om) {
        // key -> String
        var keySer = new StringRedisSerializer();

        // 其它 cache 默认用通用 JSON（无参构造自带类型信息，稳）
        var genericSer = new GenericJackson2JsonRedisSerializer();

        var defaultCfg = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(genericSer));

        // 指定 "analysis" cache 的值类型为 AnalysisResponse（命中时直接反序列化为 DTO）
        var analysisSer = new Jackson2JsonRedisSerializer<>(om, AnalysisResponse.class);
        var analysisCfg = defaultCfg.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(analysisSer)
        ).entryTtl(Duration.ofSeconds(604800));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultCfg)
                .withInitialCacheConfigurations(Map.of("analysis", analysisCfg))
                .build();
    }
}
