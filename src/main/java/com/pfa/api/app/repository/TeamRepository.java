package com.pfa.api.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.Team;
import com.pfa.api.app.entity.user.User;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByName(String teamName);
    List<Team> findByAcademicYear(String academicYear);
    List<Team> findByMembersContaining(User user);

}
