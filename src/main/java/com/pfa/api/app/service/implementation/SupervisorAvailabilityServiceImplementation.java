package com.pfa.api.app.service.implementation;

import com.pfa.api.app.entity.SupervisorAvailability;
import com.pfa.api.app.repository.SupervisorAvailabilityRepository;
import com.pfa.api.app.service.SupervisorAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


import com.pfa.api.app.dto.requests.SupervisorAvailabilityDTO;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.UserRepository;
import java.util.List;

@Service
public class SupervisorAvailabilityServiceImplementation  implements SupervisorAvailabilityService {

    @Autowired
    private SupervisorAvailabilityRepository availabilityRepository;
    @Autowired
    private UserRepository userRepository;

    public SupervisorAvailability addAvailability(SupervisorAvailabilityDTO dto) {
        User supervisor = userRepository.findById(dto.getSupervisorId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid supervisor ID"));

        SupervisorAvailability availability = SupervisorAvailability.builder()
                .supervisor(supervisor)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();

        return availabilityRepository.save(availability);
    }

    public List<SupervisorAvailability> getAvailabilitiesBySupervisor(Long supervisorId) {
        return availabilityRepository.findBySupervisorId(supervisorId);
    }
}
