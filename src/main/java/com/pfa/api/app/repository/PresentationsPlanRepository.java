package com.pfa.api.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.PresentationsPlan;

public interface PresentationsPlanRepository extends JpaRepository<PresentationsPlan, Long>{

    Optional<PresentationsPlan> findByAcademicYear(String academicYear);
    
}
