package com.pfa.api.app.dto.responses;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.Team;
import com.pfa.api.app.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamResponseDTO {
    private Long id;
    private String name;
    private List<UserResponseDTO> members;
    private UserResponseDTO responsible;
    private Long projectId;

    // You can include other fields or associations as needed

    public static TeamResponseDTO fromEntity(Team team) {
        return TeamResponseDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .members(team.getMembers().stream().map(UserResponseDTO::fromEntity).collect(Collectors.toList()))
                .responsible(UserResponseDTO.fromEntity(team.getResponsible()))
                .projectId(team.getProject() != null ? team.getProject().getId() : null)
                .build();
    }
}
