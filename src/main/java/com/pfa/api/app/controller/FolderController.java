package com.pfa.api.app.controller;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pfa.api.app.JsonRsponse.JsonResponse;
import com.pfa.api.app.dto.requests.FilesDTO;
import com.pfa.api.app.dto.requests.FolderDTO;
import com.pfa.api.app.dto.responses.DocumentResponseDTO;
import com.pfa.api.app.dto.responses.FolderResponseDTO;
import com.pfa.api.app.service.FolderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @GetMapping
    public ResponseEntity<List<FolderResponseDTO>> getAllFolders(@RequestParam Long projectId) throws NotFoundException {
        return ResponseEntity.ok(folderService.getFoldersByProjectId(projectId));
    }

    @PostMapping
    public ResponseEntity<FolderResponseDTO> createFolder(@RequestBody FolderDTO folderDTO) {
        return ResponseEntity.ok(folderService.createFolder(folderDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FolderResponseDTO> getFolderById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(folderService.getFolderById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FolderResponseDTO> updateFolder(@PathVariable Long id, @RequestBody FolderDTO folderDTO) throws NotFoundException {
        return ResponseEntity.ok(folderService.updateFolder(id, folderDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JsonResponse> deleteFolder(@PathVariable Long id) throws NotFoundException {
        folderService.deleteFolder(id);
        return ResponseEntity.ok(new JsonResponse(200,"Folder deleted successfully"));
    }

    @DeleteMapping("/{id}/delete-files")
    public ResponseEntity<JsonResponse> deleteFiles(@PathVariable Long id, @RequestBody FilesDTO files) throws NotFoundException {
        folderService.deleteFiles(id, files.getDocumentIds());
        return ResponseEntity.ok(new JsonResponse(200,"Files deleted successfully"));
    }

    @PostMapping("/{id}/upload-files")
    public ResponseEntity<List<DocumentResponseDTO>> uploadFiles(@PathVariable Long id, @RequestParam("files") List<MultipartFile> files) throws NotFoundException {
        return ResponseEntity.ok(folderService.uploadFiles(id, files));
    }
}
