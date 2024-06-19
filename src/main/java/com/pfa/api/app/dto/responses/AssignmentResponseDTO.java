package com.pfa.api.app.dto.responses;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pfa.api.app.entity.Assignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentResponseDTO {
    private Long id;
    private Boolean initiated;
    private Boolean completed;
    private Long branchId;
    private String academicYear;

    // You can include other fields or associations as needed

    public static AssignmentResponseDTO fromEntity(Assignment assignment) {
        return AssignmentResponseDTO.builder()
                .id(assignment.getId())
                .initiated(assignment.getInitiated())
                .academicYear(assignment.getAcademicYear())
                .completed(assignment.getCompleted())
                .branchId(assignment.getBranch() != null ? assignment.getBranch().getId() : null)
                .build();
    }
}
