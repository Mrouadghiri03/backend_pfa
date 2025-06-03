package com.pfa.api.app.dto.responses.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MethodDetail {
    private String visibility;
    private String name;
    private List<ParameterDetail> parameters;
    private String returnType;
}