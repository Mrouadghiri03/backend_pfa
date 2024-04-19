package com.pfa.api.app.dto.responses;

import java.util.Map;

import com.pfa.api.app.entity.user.TeamPreference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamPreferenceResponseDTO {
    private Long id;
    private UserResponseDTO user;
    private Map<Long, Integer> projectPreferenceRanks;

    // You can include other fields or associations as needed

    public static TeamPreferenceResponseDTO fromEntity(TeamPreference teamPreference) {
        return TeamPreferenceResponseDTO.builder()
                .id(teamPreference.getId())
                .user(UserResponseDTO.fromEntity(teamPreference.getUser()))
                .projectPreferenceRanks(teamPreference.getProjectPreferenceRanks())
                .build();
    }
}
