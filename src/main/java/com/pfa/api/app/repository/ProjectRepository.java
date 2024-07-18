package com.pfa.api.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.Project;

public interface ProjectRepository extends JpaRepository<Project,Long>{
    Optional<Project> findByApprovalToken(String approvalToken);
    List<Project> findByIsPublicTrue();
    Optional<Project> findByTitle(String title);
    List<Project> findByAcademicYear(String academicYear);

    
}
