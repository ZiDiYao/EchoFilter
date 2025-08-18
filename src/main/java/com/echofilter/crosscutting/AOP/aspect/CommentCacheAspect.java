package com.echofilter.crosscutting.AOP.aspect;

import com.echofilter.crosscutting.AOP.annotation.CommentCacheable;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static com.echofilter.commons.utils.text.Hash256.sha256Hex;
import static com.echofilter.commons.utils.text.TextNormalizer.normalize;

@Aspect
@Component
@RequiredArgsConstructor
public class CommentCacheAspect {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final DefaultParameterNameDiscoverer NAME_DISC = new DefaultParameterNameDiscoverer();

    @Around("@annotation(com.echofilter.crosscutting.AOP.annotation.CommentCacheable)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("CommentCacheAspect has been called");
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        CommentCacheable ann = method.getAnnotation(CommentCacheable.class);

        // 1) 生成 key
        String dynamicPart = resolveKeyBySpEL(ann.key(), method, pjp.getArgs());
        if (dynamicPart == null || dynamicPart.isEmpty()) {
            // 默认：把所有入参序列化后 normalize+hash，作为动态段
            dynamicPart = sha256Hex(normalize(objectMapper.writeValueAsString(pjp.getArgs())));
        }
        String redisKey = "cf:" + ann.name() + ":" + dynamicPart;

        // 2) 查缓存
        String cached = stringRedisTemplate.opsForValue().get(redisKey);
        if (cached != null) {
            Class<?> returnType = ((MethodSignature) pjp.getSignature()).getReturnType();
            return objectMapper.readValue(cached, returnType);
        }

        // 3) 执行业务
        Object ret = pjp.proceed();

        if (ret != null || ann.cacheNull()) {
            String json = objectMapper.writeValueAsString(ret);
            stringRedisTemplate
                    .opsForValue()
                    .set(redisKey, json, ann.ttl(), ann.unit());
        }

        return ret;
    }

    private String resolveKeyBySpEL(String spel, Method method, Object[] args) {
        if (spel == null || spel.isBlank()) return "";
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        // 允许通过 #p0/#a0 访问参数（避免编译器未加 -parameters 时拿不到参数名）
        for (int i = 0; i < args.length; i++) {
            ctx.setVariable("p" + i, args[i]);
            ctx.setVariable("a" + i, args[i]);
        }
        // 如果你项目启用了 -parameters，也可用真实名
        String[] names = NAME_DISC.getParameterNames(method);
        if (names != null) {
            for (int i = 0; i < names.length; i++) ctx.setVariable(names[i], args[i]);
        }
        Expression exp = PARSER.parseExpression(spel);
        Object val = exp.getValue(ctx);
        return val == null ? "" : val.toString();
    }
}