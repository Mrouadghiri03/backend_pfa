package com.pfa.api.app.dto.responses;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.pfa.api.app.entity.Document;
import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String academicYear;
    private String status;
    private String techStack;
    private String codeLink;
    private Boolean isPublic;
    private String approvalToken;
    private DocumentResponseDTO reportId;
    private Long branchId;
    private List<Long> supervisorIds;
    private List<DocumentResponseDTO> documentIds;
    private Long teamId;
    private Long backlog;

    // You can include other fields or associations as needed

    public static ProjectResponseDTO fromEntity(Project project) {
        return ProjectResponseDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .academicYear(project.getAcademicYear())
                .status(project.getStatus())
                .techStack(project.getTechStack())
                .codeLink(project.getCodeLink())
                .isPublic(project.getIsPublic())
                .branchId(project.getBranch() != null ? project.getBranch().getId() : null)
                .approvalToken(project.getApprovalToken())
                .reportId(project.getReport() != null ? DocumentResponseDTO.fromEntity(project.getReport()) : null)
                .supervisorIds(project.getSupervisors() != null
                ? project.getSupervisors().stream().map(User::getId).collect(Collectors.toList())
                : null)
                .documentIds(project.getDocuments() != null
                ? project.getDocuments().stream().map(DocumentResponseDTO::fromEntity).collect(Collectors.toList())
                : null)
                .teamId(project.getTeam() != null ? project.getTeam().getId() : null)
                .backlog(project.getBacklog().getId())
                .build();

        }
}
