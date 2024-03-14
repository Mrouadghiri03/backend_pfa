package com.pfa.api.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.user.User;


public interface UserRepository extends JpaRepository<User , Long>{
    Optional<User> findByEmail(String email);

}
