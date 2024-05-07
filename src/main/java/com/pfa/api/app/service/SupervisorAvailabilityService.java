package com.pfa.api.app.service;

import com.pfa.api.app.dto.requests.SupervisorAvailabilityDTO;
import com.pfa.api.app.entity.SupervisorAvailability;

import java.util.List;

public interface SupervisorAvailabilityService {
    public SupervisorAvailability addAvailability(SupervisorAvailabilityDTO dto) ;
    public List<SupervisorAvailability> getAvailabilitiesBySupervisor(Long supervisorId);

}
