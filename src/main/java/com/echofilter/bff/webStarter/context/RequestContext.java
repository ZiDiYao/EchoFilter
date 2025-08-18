package com.echofilter.bff.webStarter.context;

import lombok.Getter;

@Getter
public final class RequestContext {
    private final String traceId;
    private final String tenantId;
    private final String userId;
    private final String clientIp;
    private final boolean sampled;
    private final long startNano;

    private RequestContext(Builder b) {
        this.traceId = b.traceId;
        this.tenantId = b.tenantId;
        this.userId = b.userId;
        this.clientIp = b.clientIp;
        this.sampled = b.sampled;
        this.startNano = b.startNano == 0 ? System.nanoTime() : b.startNano;
    }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private String traceId, tenantId, userId, clientIp;
        private boolean sampled;
        private long startNano;
        public Builder traceId(String v){ this.traceId=v; return this; }
        public Builder tenantId(String v){ this.tenantId=v; return this; }
        public Builder userId(String v){ this.userId=v; return this; }
        public Builder clientIp(String v){ this.clientIp=v; return this; }
        public Builder sampled(boolean v){ this.sampled=v; return this; }
        public Builder startNano(long v){ this.startNano=v; return this; }
        public RequestContext build(){
            if (traceId == null || traceId.isBlank()) throw new IllegalStateException("traceId required");
            return new RequestContext(this);
        }
    }
}
