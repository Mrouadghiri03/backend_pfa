package com.pfa.api.app.service;

import java.util.List;

import com.pfa.api.app.dto.requests.SprintDTO;
import com.pfa.api.app.dto.responses.SprintResponse;
import com.pfa.api.app.entity.Sprint;

public interface SprintService {

    SprintResponse AddSprint(SprintDTO sprintDTO);

    SprintResponse updateSprint(SprintDTO sprintDTO , Long id);
    SprintResponse closeSprint( Long id);
    SprintResponse startSprint( Long id);

    SprintResponse getSprint(Long id);

    List<SprintResponse> getAllSprints(Long projectId);

    SprintResponse deleteSprint(Long id);
}
