package com.echofilter.bff.dto.request;

import com.echofilter.modules.dto.request.CommentRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class CommentRequestBFF {
    /** Platform name, e.g., Reddit / YouTube / X / Internal */
    @NotBlank
    @JsonProperty("platform")
    private String platform;

    /** Original post/body text (optional, useful for extra context) */
    @JsonProperty("context")
    private String context;

    /** The comment's plain text to analyze */
    @NotBlank
    @JsonProperty("content")
    private String content;

    /** The user id as defined by the platform (not your internal user id) */
    @JsonProperty("platformUserId")
    private Long platformUserId;

    /**
     * Which LLM adapter to use inside your system.
     * Externally you still accept "LLMAPI", internally we use field name llmApi.
     */
    @JsonProperty("LLMAPI")
    private String llmApi;

    /** The page URL where the comment lives (for audit and UI jump-back) */
    @NotBlank
    @JsonProperty("sourceUrl")
    private String sourceUrl;

    /** Platform-specific post/thread id, e.g., "t3_xxx" for Reddit */
    @NotBlank
    @JsonProperty("postId")
    private String postId;

    /** Platform-specific comment id, e.g., "t1_xxx" for Reddit */
    @NotBlank
    @JsonProperty("commentId")
    private String commentId;

    /**
     * Optional DOM hook to help the frontend re-anchor:
     * e.g., a data-id you inject, a DOM id, or a stable CSS selector.
     */
    @JsonProperty("domId")
    private String domId;

    /** ISO language tag if known (e.g., "en", "zh-CN") */
    @JsonProperty("language")
    private String language;

    /**
     * Text normalization rule used before analysis.
     * The frontend must use the same rule when mapping start/end offsets.
     */
    @JsonProperty("normalization")
    private CommentRequest.Normalization normalization = CommentRequest.Normalization.WHITESPACE_COLLAPSE;

    /**
     * Hash of the normalized text, e.g., "sha256:abcdef...".
     * Lets the frontend verify it is anchoring against the same version.
     */
    @JsonProperty("textHash")
    private String textHash;

    /* ===================== Enhanced anchoring fields (optional) ===================== */

    /**
     * Direct permalink to the specific comment (if the platform provides one).
     * Useful to load a focused view where the comment is guaranteed to be present.
     */
    @JsonProperty("commentPermalink")
    private String commentPermalink;

    /**
     * Parent comment id for threaded discussions (if this is a reply).
     * Helps the client expand ancestor chain before anchoring.
     */
    @JsonProperty("parentCommentId")
    private String parentCommentId;

    /**
     * Optional sort hint for loading the thread (e.g., TOP/NEW/BEST/RELEVANT/CHRONO).
     * Some platforms load different comments depending on the sort mode.
     */
    @JsonProperty("sortHint")
    private CommentRequest.SortHint sortHint;

    /**
     * Optional thread path from root to this comment, e.g., ["t1_a","t1_b","t1_c"].
     * Lets the client expand/scroll the correct branch in deeply nested threads.
     */
    @JsonProperty("threadPath")
    private List<String> threadPath;

    /**
     * Optional author handle/username on the platform, used as a last-resort cue.
     */
    @JsonProperty("authorHandle")
    private String authorHandle;

    /**
     * Optional comment creation time (ISO-8601).
     * Can be used as an additional signal when re-anchoring.
     */
    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")  // e.g., 2025-08-13T11:20:00+08:00
    private OffsetDateTime createdAt;

    public enum Normalization {
        /** Do nothing to the text */
        NONE,
        /** Collapse runs of whitespace to a single space and trim ends */
        WHITESPACE_COLLAPSE,
        /** Strip HTML tags first, then collapse whitespace */
        HTML_STRIP
    }

    public enum SortHint {
        TOP, NEW, BEST, RELEVANT, CHRONO
    }

    @JsonProperty("freshness")
    private CommentRequest.Freshness freshness;

    @Data
    public static class Freshness {
        /** 页面或客户端观察到的“当前时间”（用于解析相对时间；UTC ISO-8601） */
        @JsonProperty("asOf")
        private String asOf; // e.g. "2025-08-13T03:15:00Z"

        /** 帖子发布时间（若拿得到） */
        @JsonProperty("postCreatedAt")
        private String postCreatedAt; // ISO-8601

        /** 这条评论的发布时间（若拿得到） */
        @JsonProperty("commentCreatedAt")
        private String commentCreatedAt; // ISO-8601

        /** 页面上抓到的相对时间原文（如 “2小时前/刚刚/昨天”），便于回溯 */
        @JsonProperty("relativeTimeText")
        private String relativeTimeText;

        /** 是否疑似“突发/最新/正在发展”的话题（前端可用页面标签/频道来判断） */
        @JsonProperty("requiresFreshEvidence")
        private Boolean requiresFreshEvidence;

        /** 认为“新”的时间窗（小时），在这窗内必须查新证据 */
        @JsonProperty("freshnessWindowHours")
        private Integer freshnessWindowHours; // 如 72

        /** 首选地域/法域（影响证据来源与判断口径） */
        @JsonProperty("region")
        private String region; // e.g. "US", "CN", "EU"

        /** 检索策略：OFFLINE_ONLY / ONLINE_IF_RECENT / ONLINE_REQUIRED */
        @JsonProperty("retrievalPolicy")
        private String retrievalPolicy;
    }
}
