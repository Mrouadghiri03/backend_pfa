package com.pfa.api.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.UserStory;

public interface UserStoryRepository extends JpaRepository<UserStory, Long> {
    
}
