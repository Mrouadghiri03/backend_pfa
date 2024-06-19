package com.pfa.api.app.repository;

import com.pfa.api.app.entity.SupervisorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupervisorAvailabilityRepository extends JpaRepository<SupervisorAvailability, Long> {
    List<SupervisorAvailability> findBySupervisorId(Long supervisorId);
}
