package com.pfa.api.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.Project;

public interface ProjectRepository extends JpaRepository<Project,Long>{

    
}
