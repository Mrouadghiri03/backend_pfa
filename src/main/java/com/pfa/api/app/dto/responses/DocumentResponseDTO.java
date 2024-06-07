package com.pfa.api.app.dto.responses;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.pfa.api.app.entity.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DocumentResponseDTO {
    
    private Long id;
    private String docName;
    private Integer fileSize;
    private Date uploadDate;
    private String uploader;
    private Long projectId;
    private List<CommentResponseDTO> comments;

    public static DocumentResponseDTO fromEntity(Document document) {
        return DocumentResponseDTO.builder()
                .id(document.getId())
                .docName(document.getDocName())
                .uploadDate(document.getUploadDate())
                .uploader(document.getUploader())
                .fileSize(document.getFileSize())
                .projectId(document.getProject() != null? document.getProject().getId(): null)
                .comments(document.getComments() != null? document.getComments().stream().map(CommentResponseDTO::fromEntity).collect(Collectors.toList()): null)
                .build();
    }
}
