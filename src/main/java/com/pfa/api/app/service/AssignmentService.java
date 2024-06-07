package com.pfa.api.app.service;

import java.util.Date;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.pfa.api.app.entity.Assignment;

public interface AssignmentService {
    Assignment getAssignmentById(Long id) throws NotFoundException;
    Assignment getAssignmentByAcademicYear(String academicYear) throws NotFoundException;
    void completeAssignment() throws NotFoundException;
}
