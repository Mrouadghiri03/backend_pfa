package com.pfa.api.app.dto.responses;

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
    private List<CommentResponseDTO> comments;

    public static DocumentResponseDTO fromEntity(Document document) {
        return DocumentResponseDTO.builder()
                .id(document.getId())
                .docName(document.getDocName())
                .fileSize(document.getFileSize())
                .comments(document.getComments() != null? document.getComments().stream().map(CommentResponseDTO::fromEntity).collect(Collectors.toList()): null)
                .build();
    }
}
