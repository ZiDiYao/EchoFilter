package com.echofilter.lowerLevel.infrastructure.modules.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Rich analyzer output that can be anchored back to the page (Grammarly-style).
 * Keep your existing AnalysisResponse (V1) for backward compatibility.
 */
@Data
@JsonInclude(NON_NULL)
public class AnalysisResponseV2 {

    /** For correlation on the client */
    @JsonProperty("commentId")
    private String commentId;

    /** Normalization used for offset calculation (MUST match frontend) */
    @JsonProperty("normalization")
    private String normalization;

    /** Hash of the normalized text (version check / idempotency) */
    @JsonProperty("textHash")
    private String textHash;

    /** Fine-grained highlights with offsets & messages */
    @JsonProperty("annotations")
    private List<Annotation> annotations;

    /** Deduplicated fact/evidence bundles (referenced by annotations.factIds) */
    @JsonProperty("facts")
    private List<Fact> facts;

    /* -------- Optional summary fields to keep parity with V1 -------- */

    /** High-level type/label (e.g., "misinformation", "toxicity") */
    @JsonProperty("type")
    private String type;

    /** Overall model confidence for the summary type */
    @JsonProperty("confidence")
    private Double confidence;

    /**
     * Legacy textual facts (optional). If you still need a plain list of strings,
     * put a short bullet list here while 'facts' keeps the structured data.
     */
    @JsonProperty("factsText")
    private List<String> factsText;

    /** Trust score derived from evidence (post-calibrated) */
    @JsonProperty("trustScore")
    private Double trustScore;

    /* ------------------- Nested DTOs ------------------- */

    @Data
    public static class Annotation {
        @JsonProperty("id")
        private String id;

        /** Character offsets [start, end) over the *normalized* plain text */
        @JsonProperty("start")
        private int start;

        @JsonProperty("end")
        private int end;

        /** Text-quote selector for fuzzy re-anchoring */
        @JsonProperty("exact")
        private String exact;

        @JsonProperty("prefix")
        private String prefix;

        @JsonProperty("suffix")
        private String suffix;

        /** UI categorization & severity */
        @JsonProperty("category")
        private String category;   // e.g., "misinformation"

        @JsonProperty("severity")
        private String severity;   // e.g., "info" / "warn" / "error"

        /** Tooltip/bubble text */
        @JsonProperty("message")
        private String message;

        /** Optional suggestion text */
        @JsonProperty("suggestion")
        private String suggestion;

        /** Optional per-annotation confidence */
        @JsonProperty("confidence")
        private Double confidence;

        /** Extra metadata (rule ids, model name, etc.) */
        @JsonProperty("meta")
        private Map<String, Object> meta;

        /** Link to facts by id */
        @JsonProperty("factIds")
        private List<String> factIds;
    }

    @Data
    public static class Fact {
        @JsonProperty("id")
        private String id;

        /** The normalized claim under check */
        @JsonProperty("claim")
        private String claim;

        /** SUPPORTS / REFUTES / NOT_ENOUGH_INFO */
        @JsonProperty("verdict")
        private Verdict verdict;

        /** Calibrated confidence for the verdict */
        @JsonProperty("confidence")
        private Double confidence;

        @JsonProperty("evidence")
        private List<Evidence> evidence;

        /** Back-reference: which annotations relate to this fact (optional) */
        @JsonProperty("annotationIds")
        private List<String> annotationIds;

        @JsonProperty("meta")
        private Map<String, Object> meta;
    }

    public enum Verdict { SUPPORTS, REFUTES, NOT_ENOUGH_INFO }

    @Data
    public static class Evidence {
        @JsonProperty("url")
        private String url;

        @JsonProperty("title")
        private String title;

        @JsonProperty("snippet")
        private String snippet;

        @JsonProperty("highlight")
        private String highlight;

        @JsonProperty("score")
        private Double score;

        @JsonProperty("publishedAt")
        private String publishedAt;

        @JsonProperty("retrievedAt")
        private String retrievedAt;

        @JsonProperty("meta")
        private Map<String, Object> meta;
    }
}
