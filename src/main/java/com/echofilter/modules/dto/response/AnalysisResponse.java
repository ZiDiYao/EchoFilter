package com.echofilter.modules.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AnalysisResponse {
    private String type;
    private double confidence;
    private List<String> facts;
    private double trustScore;
}
