package com.pfa.api.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pfa.api.app.entity.user.Role;
import com.pfa.api.app.entity.user.RoleName;
import com.pfa.api.app.entity.user.User;


public interface UserRepository extends JpaRepository<User , Long>{
    Optional<User> findByEmail(String email);
    Optional<User> findByCin(String cin);
    Optional<User> findByInscriptionNumber(String inscriptionNumber);

    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
    Optional<User> findByResetCode(String token);

}
