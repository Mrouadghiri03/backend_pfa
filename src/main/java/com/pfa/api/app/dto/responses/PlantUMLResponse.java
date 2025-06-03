package com.pfa.api.app.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantUMLResponse {
    private String plantUmlCode;
    private String message;
}