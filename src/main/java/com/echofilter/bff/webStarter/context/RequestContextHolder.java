package com.echofilter.bff.webStarter.context;

public final class RequestContextHolder {
    private static final ThreadLocal<RequestContext> TL = new ThreadLocal<>();

    private RequestContextHolder(){}

    public static RequestContext get(){ return TL.get(); }

    public static Scope open(RequestContext ctx){
        RequestContext prev = TL.get();
        TL.set(ctx);
        return new Scope(prev);
    }

    public static void clear(){ TL.remove(); }

    public static final class Scope implements AutoCloseable {
        private final RequestContext prev;
        private boolean closed = false;
        private Scope(RequestContext prev){ this.prev = prev; }
        @Override public void close(){
            if (closed) return;
            if (prev == null) TL.remove(); else TL.set(prev);
            closed = true;
        }
    }
}
