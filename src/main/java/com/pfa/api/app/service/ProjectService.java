package com.pfa.api.app.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import com.pfa.api.app.dto.requests.ProjectDTO;
import com.pfa.api.app.dto.responses.ProjectResponseDTO;
import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.user.TeamPreference;
import com.pfa.api.app.entity.user.User;



public interface ProjectService {


    ProjectResponseDTO addProject(ProjectDTO projectDTO, List<MultipartFile> files ,MultipartFile report) throws AccessDeniedException, NotFoundException;

    ProjectResponseDTO getProject(Long id) throws NotFoundException;

    List<ProjectResponseDTO> getAllProjects() throws NotFoundException;

    ProjectResponseDTO updateProject(ProjectDTO projectDTO, Long id,
            List<MultipartFile> files,MultipartFile report) throws NotFoundException;

    ProjectResponseDTO deleteFile(Long id, Long docId) throws NotFoundException, IOException;

    void validateToken(String approvalToken);

    public String submitProjectPreference(Map<Long, Integer> projectPreferences) throws NotFoundException;

    List<TeamPreference> getAllProjectsPreferences();

    Map<User,Project> assignUsersToProjects() throws NotFoundException;

    void validateAssignments() throws NotFoundException;

    ResponseEntity<byte[]> downloadFile(Long projectId, Long docId);

}