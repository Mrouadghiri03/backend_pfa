package com.pfa.api.app.dto.responses;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.pfa.api.app.entity.Folder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class FolderResponseDTO {

    private Long id;
    private String name;
    private String type;
    private Long size;
    private List<DocumentResponseDTO> documents;

    public static FolderResponseDTO fromEntity(Folder folder) {
        return FolderResponseDTO.builder()
                .id(folder.getId())
                .name(folder.getName())
                .type(folder.getType())
                .size(folder.getDocuments() != null? folder.getDocuments().stream().mapToLong(document -> document.getFileSize()).sum(): 0L)
                .documents(folder.getDocuments() != null? folder.getDocuments().stream().map(DocumentResponseDTO::fromEntity).collect(Collectors.toList()): new ArrayList<>())
                .build();
    }
}
