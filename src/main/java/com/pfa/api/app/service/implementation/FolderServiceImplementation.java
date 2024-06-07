package com.pfa.api.app.service.implementation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import com.pfa.api.app.dto.requests.FolderDTO;
import com.pfa.api.app.dto.responses.DocumentResponseDTO;
import com.pfa.api.app.dto.responses.FolderResponseDTO;
import com.pfa.api.app.entity.Comment;
import com.pfa.api.app.entity.Document;
import com.pfa.api.app.entity.Folder;
import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.CommentRepository;
import com.pfa.api.app.repository.DocumentRepository;
import com.pfa.api.app.repository.FolderRepository;
import com.pfa.api.app.repository.ProjectRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.service.FolderService;
import com.pfa.api.app.util.FileUtils;
import com.pfa.api.app.util.UserUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderServiceImplementation implements FolderService {
    @Value("${upload.directory}")
    public String DIRECTORY;
    private final FolderRepository folderRepository;
    private final ProjectRepository projectRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public FolderResponseDTO getFolderById(Long id) throws NotFoundException {
        Folder folder = folderRepository.findById(id).orElseThrow(
            () -> new NotFoundException()
        );
        return FolderResponseDTO.fromEntity(folder);
    }

    @Override
    public FolderResponseDTO getFolderByName(String name) throws NotFoundException {
        Folder folder = folderRepository.findByName(name).orElseThrow(
            () -> new NotFoundException()
        );
        return FolderResponseDTO.fromEntity(folder);
    }



    @Override
    public FolderResponseDTO updateFolder(Long id, FolderDTO folder) throws NotFoundException {
        Folder folderToUpdate = folderRepository.findById(id).orElseThrow(
            () -> new NotFoundException()
        );
        if (folder.getName() != null) {
            folderToUpdate.setName(folder.getName());
        }
        Folder updatedFolder = folderRepository.save(folderToUpdate);
        return FolderResponseDTO.fromEntity(updatedFolder);
    }

    @Override
    public FolderResponseDTO createFolder(FolderDTO folder) {
        Project project = projectRepository.findById(folder.getProjectId()).orElseThrow(
            () -> new RuntimeException("Project not found")
            );
        Folder existingFolder = project.getFolders().stream()
            .filter(f -> f.getName().equals(folder.getName()))
            .findFirst()
            .orElse(null);
        if (existingFolder != null) {
            throw new RuntimeException("Folder already exists");
        }
        Folder newFolder = Folder.builder()
            .name(folder.getName())
            .project(project)
            .type("OTHER")
            .build();
        Folder savedFolder = folderRepository.save(newFolder);
        return FolderResponseDTO.fromEntity(savedFolder);
    }
    
    @Override
    public List<FolderResponseDTO> getFoldersByProjectId(Long projectId) throws NotFoundException {
        List<Folder> folders = folderRepository.findByProjectId(projectId).orElseThrow(NotFoundException::new);
        return folders.stream().map(FolderResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponseDTO> uploadFiles(Long id, List<MultipartFile> files) throws NotFoundException {
        Folder folder = folderRepository.findById(id).orElseThrow(
            () -> new RuntimeException("Folder not found")
        );
        User user = UserUtils.getCurrentUser(userRepository);
        // upload files
        List<Document> documents = FileUtils.saveDocuments(files, folder.getProject(), DIRECTORY, documentRepository, user);
        folder.setDocuments(documents);
        folderRepository.save(folder);
        for (Document doc : documents) {
            doc.setFolder(folder);
            documentRepository.save(doc);
        }
        return documents.stream().map(DocumentResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    public void deleteFolder(Long id) throws NotFoundException {
        Folder folder = folderRepository.findById(id).orElseThrow(
                () -> new NotFoundException());
        for (Document doc : folder.getDocuments()) {
            doc.setFolder(null);
            for (Comment comment : doc.getComments()) {
                comment.setDocument(null);
                commentRepository.delete(comment);
            }
            try {
                Files.deleteIfExists(
                        Paths.get(DIRECTORY + "/" + doc.getProject().getId() + "/" + doc.getDocName() + ".gz"));

                documentRepository.delete(doc);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file: " + e.getMessage());
            }
            // doc.getProject().getDocuments().remove(doc);

        }
        folderRepository.delete(folder);
    }

    @Override
    @Transactional
    public void deleteFiles(Long id, List<Long> documentIds) throws NotFoundException {
        // Retrieve the folder to which the documents belong
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Folder not found with id: " + id));

        for (Long docId : documentIds) {
            Document document = documentRepository.findById(docId)
                    .orElseThrow(() -> new RuntimeException("Document not found with id: " + docId));

            // Set the folder reference to null before deleting
            document.setFolder(null);

            // Remove and delete comments associated with the document
            for (Comment comment : document.getComments()) {
                comment.setDocument(null);
                commentRepository.delete(comment);
            }

            // Delete the document from the file system
            try {
                boolean isDeleted = Files.deleteIfExists(Paths
                        .get(DIRECTORY + "/" + document.getProject().getId() + "/" + document.getDocName() + ".gz"));
                if (!isDeleted) {
                    System.out.println("File not found: " + document.getDocName());
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file: " + e.getMessage());
            }

            // Remove the document from the project's document list before deleting
            // document.getProject().getDocuments().remove(document);

            folder.getDocuments().remove(document);
            // Delete the document from the repository
            documentRepository.delete(document);
        }

        // Save the folder to update any changes
        folderRepository.save(folder);
    }
    
}
