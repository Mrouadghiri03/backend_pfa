package com.pfa.api.app.dto.responses;
import java.util.List;
import com.pfa.api.app.entity.Backlog;
import com.pfa.api.app.entity.UserStory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BacklogResponseDTO {

    private Long id;
    private Long idProject;
    private List<UserStory> userStories;
    
    public static BacklogResponseDTO fromEntity(Backlog backlog){

        return BacklogResponseDTO.builder()
               .id(backlog.getId())
               .idProject(backlog.getProject().getId())
               .userStories(backlog.getUserStories())
               .build();
    }
    
}
