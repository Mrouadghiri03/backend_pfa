package com.pfa.api.app.repository;
import com.pfa.api.app.entity.user.TeamPreference;
import com.pfa.api.app.entity.user.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectPreferenceRepository extends JpaRepository<TeamPreference, Long> {
    Optional<TeamPreference> findPreferenceByUser(User user);
}
