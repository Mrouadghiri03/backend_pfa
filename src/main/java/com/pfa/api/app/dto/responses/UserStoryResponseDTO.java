package com.pfa.api.app.dto.responses;

import com.pfa.api.app.entity.UserStory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserStoryResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String status;
    private Long priorite;
    private Long story_points; 

    private Long developerId;

    private Long sprintId;
    private Long backlogId;

   public static UserStoryResponseDTO fromEntity(UserStory userStory) {

        return UserStoryResponseDTO.builder()
                .id(userStory.getId())
                .name(userStory.getName())
                .description(userStory.getDescription())
                .status(userStory.getStatus())
                .priorite(userStory.getPriority())
                .story_points(userStory.getStory_points())
                .developerId(userStory.getDeveloper()==null ? null : userStory.getDeveloper().getId())
                .sprintId(userStory.getSprint()==null ? null : userStory.getSprint().getId())
                .backlogId(userStory.getBacklog().getId())
                .build();
    }

    
    
    
}
