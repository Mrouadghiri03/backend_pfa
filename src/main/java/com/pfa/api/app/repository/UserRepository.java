package com.pfa.api.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pfa.api.app.entity.user.Role;
import com.pfa.api.app.entity.user.RoleName;
import com.pfa.api.app.entity.user.User;


public interface UserRepository extends JpaRepository<User , Long>{

    // Méthode principale pour l'authentification
    Optional<User> findByInscriptionNumber(String inscriptionNumber);





    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);


    @Query("SELECT u FROM User u WHERE LOWER(TRIM(u.email)) = LOWER(TRIM(:email))")
    Optional<User> findByEmail(@Param("email") String email);
    Optional<User> findByCin(String cin);
    Optional<User> findByResetCode(String token);
    @Modifying
    @Query("UPDATE User u SET u.password = :password, u.resetCode = :resetCode WHERE u.id = :id")
    void updatePasswordAndResetCode(@Param("id") Long id,
                                    @Param("password") String password,
                                    @Param("resetCode") String resetCode);

}
