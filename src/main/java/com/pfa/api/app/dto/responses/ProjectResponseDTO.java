package com.pfa.api.app.dto.responses;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pfa.api.app.entity.Branch;
import com.pfa.api.app.entity.Document;
import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.Team;
import com.pfa.api.app.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Date year;
    private String status;
    private String techStack;
    private String codeLink;
    private List<Date> dueDates;
    private Boolean isPublic;
    private String approvalToken;
    private Long reportId;
    private Long branchId;
    private List<Long> supervisorIds;
    private List<Long> documentIds;
    private Long teamId;

    // You can include other fields or associations as needed

    public static ProjectResponseDTO fromEntity(Project project) {
        return ProjectResponseDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .year(project.getYear())
                .status(project.getStatus())
                .techStack(project.getTechStack())
                .codeLink(project.getCodeLink())
                .dueDates(project.getDueDates())
                .isPublic(project.getIsPublic())
                .approvalToken(project.getApprovalToken())
                .reportId(project.getReport() != null ? project.getReport().getId() : null)
                .branchId(project.getBranch() != null ? project.getBranch().getId() : null)
                .supervisorIds(project.getSupervisors() != null
                        ? project.getSupervisors().stream().map(User::getId).collect(Collectors.toList())
                        : null)
                .documentIds(project.getDocuments() != null
                        ? project.getDocuments().stream().map(Document::getId).collect(Collectors.toList())
                        : null)
                .teamId(project.getTeam() != null ? project.getTeam().getId() : null)
                .build();
    }
}
