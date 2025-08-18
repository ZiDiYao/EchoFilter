package com.echofilter.crosscutting.Interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 统计每个 HTTP 请求的耗时，并打印日志
 */
public class TimingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TimingInterceptor.class);

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 请求进来时打点
        request.setAttribute(START_TIME, System.nanoTime());
        return true; // 返回 true 才会继续往下执行 Controller
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        Object startObj = request.getAttribute(START_TIME);
        if (startObj instanceof Long startNs) {
            long tookMs = (System.nanoTime() - startNs) / 1_000_000;

            String method = request.getMethod();
            String uri = request.getRequestURI();
            int status = response.getStatus();

            // 判断是不是 Controller 方法
            String handlerName = (handler instanceof HandlerMethod hm)
                    ? hm.getBeanType().getSimpleName() + "." + hm.getMethod().getName()
                    : handler.toString();

            // 响应头加一个 X-Response-Time
            response.addHeader("X-Response-Time", tookMs + "ms");

            if (ex == null) {
                log.info("HTTP OK method={} uri={} status={} tookMs={} handler={}",
                        method, uri, status, tookMs, handlerName);
            } else {
                String msg = ex.getMessage();
                if (msg != null && msg.length() > 200) msg = msg.substring(0, 200) + "...";
                log.error("HTTP ERR method={} uri={} status={} tookMs={} handler={} exType={} msg={}",
                        method, uri, status, tookMs, handlerName,
                        ex.getClass().getSimpleName(), msg);
            }
        }
    }
}