package com.echofilter.crosscutting.AOP.annotation;

import java.lang.annotation.*;

/**
 * 仅对打了该注解的方法记录 API 日志
 * - sample：采样率（0~1），例如 0.1 表示 10% 采样
 * - argsMax/resultMax：入参/出参日志最大字节数（防炸日志）
 * - mask：需要脱敏的字段名（不区分大小写）
 * - logHeaders：是否记录请求头（同样做脱敏）
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiLog {
    double sample() default 1.0;
    int argsMax() default 2048;
    int resultMax() default 2048;
    String[] mask() default {"authorization", "password", "pass", "token", "apikey", "apiKey", "secret"};
    boolean logHeaders() default false;
}