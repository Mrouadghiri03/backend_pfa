package com.pfa.api.app.dto.responses;

import com.pfa.api.app.entity.Presentation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PresentationResponseDTO {
    private Long Id;
    private TeamResponseDTO Team;
    private List<UserResponseDTO> JuryMembers;
    private Date StartTime;
    private Date EndTime;
    private String Location;
    private String Description;
    private String Subject;

    public static PresentationResponseDTO fromEntity(Presentation presentation) {
        String juryMembersNames = presentation.getJuryMembers().stream()
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .collect(Collectors.joining(", "));

        return PresentationResponseDTO.builder()
                .Id(presentation.getId())
                .Team(TeamResponseDTO.fromEntity(presentation.getTeam()))
                .JuryMembers(presentation.getJuryMembers().stream().map(UserResponseDTO::fromEntity).toList())
                .StartTime(presentation.getStartTime())
                .EndTime(presentation.getEndTime())
                .Description("the jury members are: " + juryMembersNames)
                .Location(presentation.getRoomNumber())
                .Subject(presentation.getTeam().getName())
                .build();
    }
}
