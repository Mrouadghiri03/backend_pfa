package com.pfa.api.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.Backlog;

public interface BacklogRepository extends JpaRepository<Backlog,Long> {
    
}
