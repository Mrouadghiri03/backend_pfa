package com.pfa.api.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pfa.api.app.dto.ProjectDTO;
import com.pfa.api.app.entity.Project;



@Service
public interface ProjectService {
    

    Project addProject(ProjectDTO  projectDTO); 
    Project getProject(Long id); 
    List<Project> getAllProject();
    Project UpdateProject(ProjectDTO  projectDTO , Long id) ;


    
    
}
