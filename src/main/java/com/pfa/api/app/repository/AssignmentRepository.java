package com.pfa.api.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.Assignment;

public interface AssignmentRepository extends JpaRepository<Assignment, Long>{
}
