package com.pfa.api.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.Document;


public interface DocumentRepository extends JpaRepository<Document , Long>{
    Optional<Document> findByDocName(String docName);
}
