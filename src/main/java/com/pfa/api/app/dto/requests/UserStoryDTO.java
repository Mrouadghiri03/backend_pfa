package com.pfa.api.app.dto.requests;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor 
public class UserStoryDTO {

    private String name;

    private String description;

    private String status;

    private Long priorite;

    private Long story_points; 

    private Long developerId;

    private Long sprintId;
    private Long backlogId;
    
}
