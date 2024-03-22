package com.pfa.api.app.dto;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ProjectDTO {


    private String title;
    
    private String description;

    private Date year;
    
    private String status;

    private String techStack;

    private String codeLink;

    private List<Date> dueDates;

    private List<Long> supervisors;

    


}
