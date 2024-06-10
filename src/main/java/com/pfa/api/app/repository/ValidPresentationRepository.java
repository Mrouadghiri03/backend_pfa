package com.pfa.api.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.ValidPresentation;

public interface ValidPresentationRepository extends JpaRepository<ValidPresentation, Long>{

    Optional<ValidPresentation> findByAcademicYear(String academicYear);
    
}
