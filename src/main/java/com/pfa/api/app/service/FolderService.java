package com.pfa.api.app.service;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

import com.pfa.api.app.dto.requests.FolderDTO;
import com.pfa.api.app.dto.responses.DocumentResponseDTO;
import com.pfa.api.app.dto.responses.FolderResponseDTO;

public interface FolderService {
    FolderResponseDTO getFolderById(Long id) throws NotFoundException;
    FolderResponseDTO getFolderByName(String name) throws NotFoundException;
    void deleteFolder(Long id) throws NotFoundException;
    
    FolderResponseDTO updateFolder(Long id, FolderDTO folder) throws NotFoundException;
    
    FolderResponseDTO createFolder(FolderDTO folder);
    List<FolderResponseDTO> getFoldersByProjectId(Long projectId) throws NotFoundException;
    List<DocumentResponseDTO> uploadFiles(Long id, List<MultipartFile> files) throws NotFoundException;
    void deleteFiles(Long id, List<Long> documentIds) throws NotFoundException;
    
}
