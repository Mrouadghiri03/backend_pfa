package com.pfa.api.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.Branch;
import com.pfa.api.app.entity.JoinRequest;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long>{
    List<JoinRequest> findAllByBranch(Branch branch);
}
