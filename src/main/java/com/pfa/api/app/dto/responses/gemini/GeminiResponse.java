package com.pfa.api.app.dto.responses.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponse {
    @JsonProperty("classes")
    private List<ClassDetail> classes;

    @JsonProperty("relationships")
    private List<RelationshipDetail> relationships;
}