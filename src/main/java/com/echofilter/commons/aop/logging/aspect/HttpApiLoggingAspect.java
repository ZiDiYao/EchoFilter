package com.echofilter.commons.aop.logging.aspect;

import com.echofilter.commons.web.context.RequestContext;
import com.echofilter.commons.web.context.RequestContextHolder;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
@Order(50) // Run after filters; low number runs earlier
public class HttpApiLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(HttpApiLoggingAspect.class);

    // ---------- Config toggles ----------
    @Value("${echofilter.logging.enabled:true}")
    private boolean enabled;

    @Value("${echofilter.logging.always-log-summary:true}")
    private boolean alwaysLogSummary; // Log IN/OUT summary even when not sampled

    @Value("${echofilter.logging.args.max-chars:2000}")
    private int maxArgChars;

    @Value("${echofilter.logging.result.max-chars:2000}")
    private int maxResultChars;

    // redact common sensitive keys in JSON-like strings
    private static final Set<String> REDACT_KEYS = Set.of(
            "password","passwd","secret","token","authorization","apiKey","apikey","access_token","refresh_token"
    );

    // types we never expand in logs
    private static final Set<Class<?>> HEAVY_ARG_TYPES = new HashSet<>(Set.of(
            byte[].class, InputStream.class, ServletRequest.class, ServletResponse.class
    ));

    // ---------- Pointcuts ----------
    /** Any public method inside classes annotated with @RestController */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void anyRestController() {}

    /** Around each controller call */
    @Around("anyRestController()")
    public Object aroundController(ProceedingJoinPoint pjp) throws Throwable {
        if (!enabled) return pjp.proceed();

        // request/response for HTTP metadata
        HttpServletRequest req = currentRequest();
        HttpServletResponse res = currentResponse();

        String http = req != null ? req.getMethod() : "-";
        String path = req != null ? req.getRequestURI() : "-";

        // method signature (class#method and parameter names)
        MethodSignature sig = (MethodSignature) pjp.getSignature();

        // sampling decision from your TraceContext
        RequestContext ctx = RequestContextHolder.get();
        boolean sampled = ctx != null && Boolean.TRUE.equals(ctx.isSampled());

        long start = System.nanoTime();

        // -------- IN (arguments) --------
        if (alwaysLogSummary || sampled) {
            String argsPreview = argsPreview(sig.getMethod().getParameters(), pjp.getArgs());
            log.info("IN  <- http={} path={} method={} sampled={} args={}",
                    http, path, sig.toShortString(), sampled, argsPreview);
        } else {
            log.info("IN  <- http={} path={} method={} sampled={}",
                    http, path, sig.toShortString(), sampled);
        }

        try {
            Object ret = pjp.proceed();
            long tookMs = (System.nanoTime() - start) / 1_000_000;
            int status = res != null ? res.getStatus() : 200;

            // -------- OUT (result) --------
            if (alwaysLogSummary || sampled) {
                String resultPreview = truncateAndRedact(safeToString(ret), maxResultChars);
                log.info("OUT -> status={} tookMs={} result={}", status, tookMs, resultPreview);
            } else {
                log.info("OUT -> status={} tookMs={}", status, tookMs);
            }
            return ret;
        } catch (Throwable t) {
            long tookMs = (System.nanoTime() - start) / 1_000_000;
            int status = res != null ? res.getStatus() : 500;
            log.error("ERR !! status={} tookMs={} exType={} msg={}",
                    status, tookMs, t.getClass().getSimpleName(), truncateAndRedact(safeMsg(t), 500));
            throw t;
        }
    }

    // ---------- Helpers ----------

    private HttpServletRequest currentRequest() {
        var attrs = (ServletRequestAttributes)
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    private HttpServletResponse currentResponse() {
        var attrs = (ServletRequestAttributes)
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getResponse() : null;
    }

    /** Build a compact, safe preview of method arguments. */
    private String argsPreview(Parameter[] params, Object[] args) {
        if (args == null || args.length == 0) return "[]";
        String body = "";
        try {
            body = Arrays.stream(new int[args.length]).mapToObj(i -> i) // indices
                    .map(i -> {
                        String name = (params != null && i < params.length && params[i].getName() != null)
                                ? params[i].getName() : "arg" + i;
                        Object val = args[i];
                        if (val == null) return name + "=null";
                        if (isHeavy(val)) return name + "=\"<" + val.getClass().getSimpleName() + ">\"";
                        if (val instanceof HttpServletRequest) return name + "=\"<HttpServletRequest>\"";
                        if (val instanceof HttpServletResponse) return name + "=\"<HttpServletResponse>\"";
                        if (val instanceof org.springframework.web.multipart.MultipartFile)
                            return name + "=\"<MultipartFile>\"";
                        String s = truncateAndRedact(safeToString(val), maxArgChars);
                        return name + "=\"" + escapeQuotes(s) + "\"";
                    })
                    .collect(Collectors.joining(", "));
        } catch (Exception e) {
            body = "<args-preview-error:" + e.getClass().getSimpleName() + ">";
        }
        return "[" + body + "]";
    }

    private boolean isHeavy(Object o) {
        if (o == null) return false;
        Class<?> c = o.getClass();
        if (HEAVY_ARG_TYPES.contains(c)) return true;
        // treat arrays of heavy types as heavy
        return c.isArray() && HEAVY_ARG_TYPES.contains(c.getComponentType());
    }

    private String safeToString(Object o) {
        if (o == null) return "null";
        // Avoid exploding logs on large collections by relying on simple toString();
        String s = String.valueOf(o);
        return redactJsonLike(s);
    }

    private String truncateAndRedact(String s, int max) {
        if (s == null) return null;
        String t = s.length() <= max ? s : s.substring(0, max) + "...(truncated " + (s.length() - max) + " chars)";
        return redactJsonLike(t);
    }

    /** Very light-weight redaction for JSON-like strings and key=value patterns. */
    private String redactJsonLike(String s) {
        String r = s;
        for (String k : REDACT_KEYS) {
            // "key":"value"  -> "key":"***"
            r = r.replaceAll("(?i)(\"" + java.util.regex.Pattern.quote(k) + "\"\\s*:\\s*\")([^\"]+)(\")",
                    "$1***$3");

            // key=value -> key=***
            r = r.replaceAll("(?i)(" + java.util.regex.Pattern.quote(k) + "\\s*=\\s*)([^&\\s]+)",
                    "$1***");
        }
        return r;
    }


    private String escapeQuotes(String s) {
        return s.replace("\"", "\\\"");
    }

    private String safeMsg(Throwable t) {
        String m = t.getMessage();
        return m == null ? "" : m;
    }
}