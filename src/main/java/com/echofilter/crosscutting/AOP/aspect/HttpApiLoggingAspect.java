package com.echofilter.crosscutting.AOP.aspect;

import com.echofilter.crosscutting.AOP.annotation.ApiLog;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

// Controller 入参/出参/耗时/异常
/** 仅拦截带 @ApiLog 的方法；支持采样、脱敏、限长、可选记录请求头 */
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class HttpApiLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(HttpApiLoggingAspect.class);

    private static final ObjectMapper M = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    /** 只匹配带 @ApiLog 的方法，并把注解对象注入到 advice 参数 */
    @Pointcut("@annotation(apiLog)")
    public void apiLogPointcut(ApiLog apiLog) {}

    @Around("apiLogPointcut(apiLog)")
    public Object logHttp(ProceedingJoinPoint pjp, ApiLog apiLog) throws Throwable {
        long startNs = System.nanoTime();
        HttpServletRequest req = currentRequest();

        String method = req != null ? req.getMethod() : "";
        String uri    = req != null ? req.getRequestURI() : "";
        String ip     = req != null ? clientIp(req) : "";

        MethodSignature ms = (MethodSignature) pjp.getSignature();
        String handler = ms.getDeclaringType().getSimpleName() + "." + ms.getName();

        String argsStr = safeArgs(pjp.getArgs(), apiLog.mask(), apiLog.argsMax());
        String headersStr = "";
        if (apiLog.logHeaders() && req != null) {
            headersStr = safeHeaders(req, apiLog.mask(), apiLog.argsMax());
        }

        Throwable error = null;
        Object ret = null;
        try {
            ret = pjp.proceed();
            return ret;
        } catch (Throwable ex) {
            error = ex;
            throw ex;
        } finally {
            long tookMs = (System.nanoTime() - startNs) / 1_000_000;

            // ① 返回头加入耗时
            try {
                var ra = RequestContextHolder.getRequestAttributes();
                if (ra instanceof ServletRequestAttributes sra && sra.getResponse() != null) {
                    sra.getResponse().addHeader("X-Response-Time", tookMs + "ms");
                }
            } catch (Exception ignore) {}

            // ② MDC 里加入耗时（如果你的 logback pattern 有 %X{tookMs} 就能自动打出来）
            try { org.slf4j.MDC.put("tookMs", String.valueOf(tookMs)); } catch (Exception ignore) {}

            // ③ 统一打印
            if (error == null) {
                String resultStr = briefJson(ret, apiLog.resultMax());
                if (apiLog.logHeaders()) {
                    log.info("API OK method={} uri={} ip={} handler={} tookMs={} headers={} args={} result={}",
                            method, uri, ip, handler, tookMs, headersStr, argsStr, resultStr);
                } else {
                    log.info("API OK method={} uri={} ip={} handler={} tookMs={} args={} result={}",
                            method, uri, ip, handler, tookMs, argsStr, resultStr);
                }
            } else {
                String msg = error.getMessage();
                msg = msg == null ? "" : brief(msg, 400);
                if (apiLog.logHeaders()) {
                    log.error("API ERR method={} uri={} ip={} handler={} tookMs={} exType={} msg={} headers={} args={}",
                            method, uri, ip, handler, tookMs, error.getClass().getSimpleName(), msg, headersStr, argsStr);
                } else {
                    log.error("API ERR method={} uri={} ip={} handler={} tookMs={} exType={} msg={} args={}",
                            method, uri, ip, handler, tookMs, error.getClass().getSimpleName(), msg, argsStr);
                }
            }

            // ④ 结束后清理 MDC 字段（可选）
            try { org.slf4j.MDC.remove("tookMs"); } catch (Exception ignore) {}
        }
    }


    // ---------- helpers ----------

    /** 当前线程里的 HttpServletRequest（异步线程可能拿不到） */
    private HttpServletRequest currentRequest() {
        var ra = RequestContextHolder.getRequestAttributes();
        return (ra instanceof ServletRequestAttributes sra) ? sra.getRequest() : null;
    }

    /** 尝试从代理头里解析真实客户端 IP */
    private String clientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) return xff.split(",")[0].trim();
        String rip = req.getHeader("X-Real-IP");
        return StringUtils.hasText(rip) ? rip : req.getRemoteAddr();
    }

    /** 入参安全序列化：跳过不适合打印的对象 → JSON → 脱敏 → 限长 */
    private String safeArgs(Object[] args, String[] maskFields, int maxChars) {
        try {
            List<Object> list = new ArrayList<>();
            for (Object a : args) {
                if (a == null) { list.add(null); continue; }
                String n = a.getClass().getSimpleName();
                if (n.contains("Multipart") || n.contains("InputStream") || n.contains("Servlet")) {
                    list.add(n); // 用类型名占位，避免序列化
                } else {
                    list.add(a);
                }
            }
            String json = M.writeValueAsString(list);
            json = maskJson(json, maskFields);
            return brief(json, maxChars);
        } catch (Exception e) {
            return "[[unprintable-args]]";
        }
    }

    /** 请求头安全打印：JSON → 脱敏 → 限长 */
    private String safeHeaders(HttpServletRequest req, String[] maskFields, int maxChars) {
        try {
            Map<String, String> m = new LinkedHashMap<>();
            Enumeration<String> names = req.getHeaderNames();
            while (names != null && names.hasMoreElements()) {
                String k = names.nextElement();
                m.put(k, req.getHeader(k));
            }
            String json = M.writeValueAsString(m);
            json = maskJson(json, maskFields);
            return brief(json, maxChars);
        } catch (Exception e) {
            return "[[unprintable-headers]]";
        }
    }

    /** 返回体序列化 + 限长（失败则只打印类名） */
    private String briefJson(Object o, int maxChars) {
        if (o == null) return "null";
        try { return brief(M.writeValueAsString(o), maxChars); }
        catch (JsonProcessingException e) { return o.getClass().getSimpleName(); }
    }

    /** 统一限长（按字符） */
    private String brief(String s, int maxChars) {
        if (s == null) return "";
        return s.length() <= maxChars ? s : s.substring(0, maxChars) + "...(" + s.length() + " chars)";
    }

    /** 按字段名（不区分大小写）递归脱敏 JSON */
    private String maskJson(String json, String[] maskFields) {
        try {
            JsonNode node = M.readTree(json);
            Set<String> mask = new HashSet<>();
            for (String f : maskFields) mask.add(f.toLowerCase());
            maskNode(node, mask);
            return M.writeValueAsString(node);
        } catch (Exception e) {
            // 失败就返回原串，避免影响主流程
            return json;
        }
    }
    private void maskNode(JsonNode node, Set<String> mask) {
        if (node == null || node.isNull()) return;
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> it = obj.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> e = it.next();
                String key = e.getKey();
                JsonNode val = e.getValue();
                if (mask.contains(key.toLowerCase())) {
                    obj.put(key, "***");
                } else {
                    maskNode(val, mask);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode item : node) maskNode(item, mask);
        }
    }
}