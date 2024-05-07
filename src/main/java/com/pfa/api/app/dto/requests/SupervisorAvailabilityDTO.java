package com.pfa.api.app.dto.requests;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SupervisorAvailabilityDTO {
    private Long supervisorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
