package com.pfa.api.app.dto.requests;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamPreferenceDTO {
    private Long id;
    private Map<Long, Integer> projectPreferenceRanks;
}
