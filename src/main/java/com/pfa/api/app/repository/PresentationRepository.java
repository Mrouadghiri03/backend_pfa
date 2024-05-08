package com.pfa.api.app.repository;

import com.pfa.api.app.entity.Presentation;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PresentationRepository extends JpaRepository<Presentation, Long> {
    @Query("SELECT p FROM Presentation p WHERE YEAR(p.endTime) = :year")
    Optional<List<Presentation>> findAllByYear(int year);
}
