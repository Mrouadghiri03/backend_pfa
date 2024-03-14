package com.pfa.api.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.user.Confirmation;

public interface ConfirmationRepository extends JpaRepository<Confirmation , Long>{
    Confirmation findByToken(String token);
}
