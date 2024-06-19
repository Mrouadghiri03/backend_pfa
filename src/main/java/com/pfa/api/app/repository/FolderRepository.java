package com.pfa.api.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pfa.api.app.entity.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long>{
    Optional<Folder> findByName(String name);

    @Query("SELECT f FROM Folder f WHERE f.project.id = ?1")
    Optional<List<Folder>> findByProjectId(Long projectId);
}
