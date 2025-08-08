package com.echofilter.modules.dto.response;

import java.util.List;

public class AnalysisResponse {
    private String type;
    private double confidence;
    private List<String> facts;
    private double trustScore;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public List<String> getFacts() {
        return facts;
    }

    public void setFacts(List<String> facts) {
        this.facts = facts;
    }

    public double getTrustScore() {
        return trustScore;
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