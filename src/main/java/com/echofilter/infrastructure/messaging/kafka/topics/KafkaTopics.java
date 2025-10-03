package com.echofilter.infrastructure.messaging.kafka.topics;

public final class KafkaTopics {
    private KafkaTopics() {} // 禁止实例化

    // 规范命名：<domain>.<stream>.<version>
    public static final String REQ     = "ef.requests.v1";
    public static final String RES     = "ef.results.v1";
    public static final String REQ_DLT = "ef.requests.DLT.v1";
    public static final String RES_DLT = "ef.results.DLT.v1";

    public static String withEnvPrefix(String topic, String env) {
        return (env == null || env.isBlank()) ? topic : (env + "." + topic);
    }


    public static String keyForRequest(String commentId, String taskId) {
        return commentId != null ? commentId : taskId; // 优先 commentId
    }
}
