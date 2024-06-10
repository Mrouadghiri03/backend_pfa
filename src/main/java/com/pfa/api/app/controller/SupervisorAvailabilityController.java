package com.pfa.api.app.controller;

import com.pfa.api.app.JsonRsponse.JsonResponse;
import com.pfa.api.app.dto.requests.SupervisorAvailabilityDTO;
import com.pfa.api.app.entity.SupervisorAvailability;
import com.pfa.api.app.service.SupervisorAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/supervisors")
public class SupervisorAvailabilityController {

    @Autowired
    private SupervisorAvailabilityService service;

    @PostMapping("/availability")
    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR')")
    public ResponseEntity<JsonResponse> addAvailability(@RequestBody SupervisorAvailabilityDTO availabilityDTO) {
        SupervisorAvailability availability = service.addAvailability(availabilityDTO);
//        return ResponseEntity.ok(availability);
        return new ResponseEntity<JsonResponse>(
                new JsonResponse(200,
                        "supervisor availability added successfully!"),
        HttpStatus.CREATED

        );
    }

    @GetMapping("/availability/{supervisorId}")
    @PreAuthorize("hasAuthority('ROLE_HEAD_OF_BRANCH')")
    public ResponseEntity<List<SupervisorAvailability>> getAvailabilities(@PathVariable Long supervisorId) {
        List<SupervisorAvailability> availabilities = service.getAvailabilitiesBySupervisor(supervisorId);
        return ResponseEntity.ok(availabilities);
    }
}
