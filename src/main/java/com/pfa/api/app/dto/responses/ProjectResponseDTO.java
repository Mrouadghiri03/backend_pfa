package com.pfa.api.app.dto.responses;

import java.util.List;
import java.util.stream.Collectors;

import com.pfa.api.app.entity.Folder;
import com.pfa.api.app.entity.Project;

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
    private List<FolderResponseDTO> folders;
    private DocumentResponseDTO report;
    private Long branchId;
    private List<Long> supervisorIds;
    private List<DocumentResponseDTO> documents;
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
                .folders(project.getFolders() != null ? project.getFolders().stream().map(FolderResponseDTO::fromEntity).collect(Collectors.toList()) : null)
                .backlog(project.getBacklog() != null ? project.getBacklog().getId() : null)
                .branchId(project.getBranch() != null ? project.getBranch().getId() : null)
                .approvalToken(project.getApprovalToken())
                .report(project.getFolders() != null ? project.getFolders().stream().filter(folder -> folder.getType().equals("REPORT")).map(Folder::getDocuments).flatMap(List::stream).map(DocumentResponseDTO::fromEntity).findFirst().orElse(null) : null)
                .supervisorIds(project.getSupervisors() != null
                ? project.getSupervisors().stream().map(user -> user.getId()).collect(Collectors.toList())
                : null)
                .documents(project.getFolders() != null
                ? project.getFolders().stream().filter(folder -> folder.getType().equals("DOCUMENTS")).map(Folder::getDocuments).flatMap(List::stream).map(DocumentResponseDTO::fromEntity).collect(Collectors.toList())
                : null)
                .teamId(project.getTeam() != null ? project.getTeam().getId() : null)
                .build();

        }
}
