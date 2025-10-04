package com.echofilter.lowerLevel.infrastructure.modules.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalysisResponse {
    private String type;
    private double confidence;
    private List<String> facts;
    private double trustScore;

    public void setType(String type) {
        this.type = type;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public void setFacts(List<String> facts) {
        this.facts = facts;
    }

    public void setTrustScore(double trustScore) {
        this.trustScore = trustScore;
    }

    @Override
    public String toString() {
        return "AnalysisResponse{" +
                "type='" + type + '\'' +
                ", confidence=" + confidence +
                ", facts=" + facts +
                ", trustScore=" + trustScore +
                '}';
    }
}