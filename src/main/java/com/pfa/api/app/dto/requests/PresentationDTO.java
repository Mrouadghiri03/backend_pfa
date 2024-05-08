package com.pfa.api.app.dto.requests;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class PresentationDTO {
    private Long teamId;  // We only use ID references in DTOs
    private List<Long> juryMemberIds;  // List of IDs for simplicity
    private Date startTime;
    private Date endTime;
    private String roomNumber;
}
