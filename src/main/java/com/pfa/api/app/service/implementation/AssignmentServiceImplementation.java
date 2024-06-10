package com.pfa.api.app.service.implementation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.pfa.api.app.entity.Assignment;
import com.pfa.api.app.repository.AssignmentRepository;
import com.pfa.api.app.service.AssignmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImplementation implements AssignmentService {

    private final AssignmentRepository assignmentRepository;


    @Override
    public Assignment getAssignmentById(Long id) throws NotFoundException {
        // Retrieve assignment by its ID from the database
        return assignmentRepository.findById(id)
                .orElseThrow(NotFoundException::new); // Return null if not found
    }

    public Assignment getAssignmentByAcademicYear(String academicYear) throws NotFoundException {

        return assignmentRepository.findByAcademicYear(academicYear).orElseThrow(NotFoundException::new);
    }

    @Override
    public void completeAssignment() throws NotFoundException {
        String academicYear = "";
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        
        if (month >= 9 && month <= 12) {
            academicYear = year + "/" + (year + 1);
        } else if (month >= 1 && month <= 7) {
            academicYear = (year - 1) + "/" + year;
        }
        Assignment assignment = getAssignmentByAcademicYear(academicYear);
        assignment.setCompleted(true);
        assignmentRepository.save(assignment);
    }
}
