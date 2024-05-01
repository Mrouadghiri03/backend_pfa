package com.pfa.api.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.Sprint;

public interface SprintRepository extends JpaRepository<Sprint,Long> {
    
}
