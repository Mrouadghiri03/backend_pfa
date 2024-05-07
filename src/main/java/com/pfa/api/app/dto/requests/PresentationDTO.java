package com.pfa.api.app.dto.requests;
//
//import com.pfa.api.app.entity.Presentation;
//import com.pfa.api.app.entity.Team;
//import com.pfa.api.app.entity.user.User;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Builder
//@Data
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class PresentationDTO {
//    private Long id;
//    private Team team;
//    private List<User> juryMembers;
//    private LocalDate scheduledDate;
//    private LocalTime scheduledTime;
//    private String roomNumber;
//
//
//}

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class PresentationDTO {
//    private Long id;
    private Long teamId;  // We only use ID references in DTOs
    private List<Long> juryMemberIds;  // List of IDs for simplicity
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private String roomNumber;
}
