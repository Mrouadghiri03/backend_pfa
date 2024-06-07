package com.pfa.api.app.dto.responses;

import java.util.List;
import java.util.stream.Collectors;

import com.pfa.api.app.entity.Team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private ProjectResponseDTO project;
    private String academicYear;

    // You can include other fields or associations as needed

    public static TeamResponseDTO fromEntity(Team team) {
        return TeamResponseDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .members(team.getMembers().stream().map(UserResponseDTO::fromEntity).collect(Collectors.toList()))
                .responsible(UserResponseDTO.fromEntity(team.getResponsible()))
                .project(team.getProject() != null ? ProjectResponseDTO.fromEntity(team.getProject()) : null)
                .academicYear(team.getAcademicYear())
                .build();
    }
}
