package com.pfa.api.app.dto.responses.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassDetail {
    private String name;
    private String stereotype;
    private List<AttributeDetail> attributes;
    private List<MethodDetail> methods;
}