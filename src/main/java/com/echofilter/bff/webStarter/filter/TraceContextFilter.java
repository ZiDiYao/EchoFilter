package com.echofilter.bff.webStarter.filter;

import com.echofilter.bff.webStarter.context.RequestContext;
import com.echofilter.bff.webStarter.context.RequestContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * TraceContextFilter
 *
 * Purpose:
 * - At the very beginning of an HTTP request, attach a unified context
 *   (traceId / tenantId / userId / clientIp / sampled) for the whole call chain.
 * - Store it in ThreadLocal (RequestContextHolder) and logging MDC.
 * - Echo the chosen traceId back to the client (X-Trace-Id / X-Request-Id).
 * - Run only once per request (skip async/error dispatch) to avoid duplicate work.
 *
 * Works with AOP:
 * - Any aspect can read RequestContextHolder.get() to tag logs/metrics,
 *   do per-user rate limiting, cost accounting, etc.
 */
@Component
@Order(0) // Run as early as possible
public class TraceContextFilter extends OncePerRequestFilter {

    /** Accepted inbound correlation headers (checked in order) */
    private static final List<String> REQ_ID_HEADERS = List.of(
            "traceparent",                 // W3C Trace Context
            "X-Request-Id", "X-Request-ID",
            "X-Correlation-Id", "Correlation-Id",
            "Request-Id",
            "ID"                           // Last-resort compatibility
    );

    /** Safe value pattern for IDs to prevent log injection / pollution */
    private static final Pattern SAFE = Pattern.compile("^[A-Za-z0-9_.\\-]{1,128}$");

    /** Deterministic sampling rate (0..1). Default: 10% */
    @Value("${echofilter.tracing.sample-rate:0.10}")
    private double sampleRate;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // 1) Read inbound request id (wide input); if none/invalid → generate UUID
        String inbound = inboundRequestId(req);
        String traceId = (inbound != null) ? inbound : UUID.randomUUID().toString();

        // 2) Deterministic sampling decision based on traceId (consistent across services/retries)
        boolean sampled = shouldSample(traceId, sampleRate);

        // 3) Labels from request
        String clientIp = Optional.ofNullable(req.getHeader("X-Forwarded-For"))
                .map(x -> x.split(",", 2)[0].trim())
                .orElseGet(req::getRemoteAddr);
        String tenantId = req.getHeader("X-Tenant-Id");
        String userId   = req.getHeader("X-User-Id");

        // 4) Build immutable request context
        RequestContext ctx = RequestContext.builder()
                .traceId(traceId)
                .tenantId(tenantId)
                .userId(userId)
                .clientIp(clientIp)
                .sampled(sampled)
                .startNano(System.nanoTime())
                .build();

        // 5) Open scope → put into ThreadLocal + MDC; echo IDs back to client
        try (var scope = RequestContextHolder.open(ctx);
             Closeable c1 = MDC.putCloseable("traceId", traceId);
             Closeable c2 = mdcIfPresent("tenantId", tenantId);
             Closeable c3 = mdcIfPresent("userId",   userId)) {

            // Strict output: always return the chosen ID so clients can correlate
            res.setHeader("X-Trace-Id", traceId);
            res.setHeader("X-Request-Id", traceId);

            chain.doFilter(req, res);
        }
    }

    /** Run only on the first (original) dispatch; skip async re-dispatch. */
    @Override protected boolean shouldNotFilterAsyncDispatch() { return true; }

    /** Run only on the first (original) dispatch; skip error re-dispatch. */
    @Override protected boolean shouldNotFilterErrorDispatch() { return true; }

    /** Skip health/actuator/static endpoints. */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/actuator") || uri.startsWith("/static") || uri.startsWith("/health");
    }

    // ----------------- helpers -----------------

    /**
     * Read inbound request id by scanning a list of known headers (wide input).
     * - For 'traceparent', extract the 32-hex trace-id part.
     * - For custom headers, validate against SAFE pattern.
     * Returns null if nothing valid is found.
     */
    private static String inboundRequestId(HttpServletRequest req) {
        for (String h : REQ_ID_HEADERS) {
            String v = req.getHeader(h);
            if (v == null || v.isBlank()) continue;

            if ("traceparent".equalsIgnoreCase(h)) {
                String tid = extractFromTraceparent(v.trim());
                if (tid != null) return tid;
                continue;
            }
            v = v.trim();
            if (SAFE.matcher(v).matches()) return v;
        }
        return null;
    }

    /**
     * Parse W3C 'traceparent': version-traceId-spanId-flags (e.g. 00-<32hex>-<16hex>-01).
     * Return the 32-hex trace-id or null if malformed.
     */
    private static String extractFromTraceparent(String tp) {
        String[] parts = tp.split("-");
        if (parts.length >= 4 && parts[1].matches("^[0-9a-fA-F]{32}$")) {
            return parts[1].toLowerCase();
        }
        return null;
    }

    /**
     * Deterministic sampling based on traceId hash.
     * Ensures consistent decision across services and retries.
     */
    private static boolean shouldSample(String traceId, double rate) {
        if (rate <= 0) return false;
        if (rate >= 1) return true;
        final int base = 10_000; // 0.01% precision
        int bucket = Math.floorMod(traceId.hashCode(), base);
        return bucket < (int) (rate * base);
    }

    /**
     * Put value into MDC if present; otherwise return a no-op Closeable
     * so try-with-resources remains simple and safe.
     */
    private static Closeable mdcIfPresent(String key, String value) {
        if (value == null || value.isBlank()) return () -> {};
        return MDC.putCloseable(key, value);
    }
}
