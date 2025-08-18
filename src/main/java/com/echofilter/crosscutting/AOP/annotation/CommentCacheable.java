package com.echofilter.crosscutting.AOP.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommentCacheable {

    /** 分组/前缀，比如 "analysis" */
    String name();

    /** SpEL 表达式，建议直接用生成的 hash；如无则给空串 */
    String key() default "";

    /** 过期时间，默认 5 分钟 */
    long ttl() default 300;

    /** 时间单位，默认秒 */
    TimeUnit unit() default TimeUnit.SECONDS;

    /** 是否缓存 null 结果 */
    boolean cacheNull() default true;
}
