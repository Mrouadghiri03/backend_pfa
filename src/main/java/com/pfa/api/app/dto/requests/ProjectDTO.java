package com.pfa.api.app.dto.requests;

import java.util.Date;
import java.util.List;

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

    private String academicYear;

    private String status;

    private String techStack;

    private String codeLink;

    private Boolean isPublic;

    private Long branch;

    private List<Long> supervisors;

    


}
