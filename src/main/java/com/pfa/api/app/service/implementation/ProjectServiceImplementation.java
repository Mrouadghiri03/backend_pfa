package com.pfa.api.app.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.pfa.api.app.dto.ProjectDTO;
import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.ProjectRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.service.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImplementation implements ProjectService {

    private final UserRepository  userRepository;
    private final ProjectRepository  projectRepository;

    @Override
    public Project addProject(ProjectDTO projectDTO) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Long id =((User) userDetails).getId();
            User  owner=userRepository.findById(id).get();
           
             List <User> supervisors = new ArrayList<>();
             supervisors.add(owner);
             if (!projectDTO.getSupervisors().isEmpty()) {
                for (Long userId  : projectDTO.getSupervisors()) {
                    if (userRepository.existsById(userId)){
 
                     User  supervisor = userRepository.findById(userId).get();
                     supervisors.add(supervisor);
 
                    } 
            }
             }
          
           Project  project = Project.builder()
           .title(projectDTO.getTitle())
           .description(projectDTO.getDescription())
           .year(projectDTO.getYear())
           .supervisors(supervisors)
           .codeLink(projectDTO.getCodeLink())
           .status(projectDTO.getStatus())
           .isPublic(false)
           .build();
           
           return projectRepository.save(project);

          
    }

    @Override
    public Project getProject(Long id) {
      return  projectRepository.findById(id).get();
    }

    @Override
    public List<Project> getAllProject() {
      
       return projectRepository.findAll();
    }

    @Override
    public Project UpdateProject(ProjectDTO projectDTO, Long id) {
        Project project = projectRepository.findById(id).get();
        //  List<User> updatedSupervisors = new ArrayList<>() ;
        //  List<Long>dtoSupervisorIds = projectDTO.getSupervisors();
        if (!projectDTO.getDescription().equals(null)) {
            project.setDescription(projectDTO.getDescription());
        }
        if (!projectDTO.getTitle().equals(null)) {
            project.setTitle(projectDTO.getTitle());
                }
        if (!projectDTO.getCodeLink().equals(null)) {
                    project.setCodeLink(projectDTO.getCodeLink());
                }
        if (!projectDTO.getStatus().equals(null)) {
                    project.setStatus(projectDTO.getTitle());
                }
        if (!projectDTO.getTechStack().equals(null)) {
                    project.setTechStack(projectDTO.getTitle());
                }
 
        projectRepository.save(project);
        return project;
       
    }

}
