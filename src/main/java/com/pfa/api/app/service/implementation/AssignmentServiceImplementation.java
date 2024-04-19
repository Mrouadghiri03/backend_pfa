package com.pfa.api.app.service.implementation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public Assignment getAssignmentByYear(String year) throws NotFoundException {
        int targetYear;
        try {
            targetYear = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid year format");
        }

        List<Assignment> assignments = assignmentRepository.findAll();

        Optional<Assignment> matchingAssignment = assignments.stream()
                .filter(assignment -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(assignment.getDate());
                    int assignmentYear = calendar.get(Calendar.YEAR);
                    return assignmentYear == targetYear;
                })
                .findFirst();

        return matchingAssignment.orElseThrow(NotFoundException::new);
    }

    @Override
    public void completeAssignment() throws NotFoundException {
        Year currentYear = Year.now();
        Assignment assignment = getAssignmentByYear(String.valueOf(currentYear));
        assignment.setCompleted(true);
        assignmentRepository.save(assignment);
    }
}
