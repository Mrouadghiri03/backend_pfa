package com.pfa.api.app.dto.responses;

import java.util.ArrayList;
import java.util.List;

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
    private Long priority;
    private Long storyPoints; 
    private List<Long> tags;
    private UserResponseDTO developer;
    private String developerName;
    private String developerImageProfile;
    private String developerId;
    private Long sprintId;
    private Long backlogId;

   public static UserStoryResponseDTO fromEntity(UserStory userStory) {

        return UserStoryResponseDTO.builder()
            .id(userStory.getId())
            .name(userStory.getName())
            .description(userStory.getDescription())
            .status(userStory.getStatus())
            .developerId(userStory.getDeveloper()==null ? null : ""+userStory.getDeveloper().getId())
            .developerImageProfile(userStory.getDeveloper()==null ? null : userStory.getDeveloper().getProfileImage())
            .tags(new ArrayList<>(List.of(userStory.getPriority(), userStory.getStoryPoints())))
            .priority(userStory.getPriority())
            .storyPoints(userStory.getStoryPoints())
            .developerName(userStory.getDeveloper()==null ? null : userStory.getDeveloper().getFirstName() + " " + userStory.getDeveloper().getLastName())
            .developer(userStory.getDeveloper()==null ? null : UserResponseDTO.fromEntity(userStory.getDeveloper()))
            .sprintId(userStory.getSprint()==null ? null : userStory.getSprint().getId())
            .backlogId(userStory.getBacklog().getId())
            .build();
    }
    


    
    
    
}
