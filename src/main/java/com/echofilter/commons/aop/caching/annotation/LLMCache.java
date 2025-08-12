package com.echofilter.commons.aop.caching.annotation;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LLMCache {
    String cacheName() default "echofilter:comment";   // 前缀
    String version() default "v1";                     // 模板/算法版本，升级时变更可一键失效旧缓存
    int ttlSeconds() default 3600;                     // 命中缓存 TTL
    int nullTtlSeconds() default 60;                   // 空结果 TTL（防穿透）
    boolean cacheNull() default true;                  // 是否缓存空结果
    boolean includeUser() default false;               // key 是否包含 userId
    int paramIndex() default 0;                        // CommentRequest 在入参中的位置
    String bypassMdcKey() default "cacheBypass";       // MDC/请求头里为 true 则绕过缓存
    int lockSeconds() default 10;                      // 击穿保护的小锁过期时间
}
