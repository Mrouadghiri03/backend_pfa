package com.pfa.api.app.controller;

import java.lang.module.ResolutionException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfa.api.app.JsonRsponse.JsonResponse;
import com.pfa.api.app.dto.ProjectDTO;
import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.service.ProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
      private final ProjectService  projectService;
      @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
      @PostMapping()
      public ResponseEntity<JsonResponse> createNewProject(@RequestBody ProjectDTO  projectDTO){
      
          Project project = projectService.addProject(projectDTO);
         return  new ResponseEntity<JsonResponse>(
            new JsonResponse(201, "Project has  been created successfully!")
         , HttpStatus.CREATED);
      }





      @GetMapping()
         public ResponseEntity<List<Project>> getProjects() {
         List<Project> projects = projectService.getAllProject();
        
         return ResponseEntity.ok(projects);
      }
      


      @GetMapping("/{id}")
      public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
         Project project = projectService.getProject(id);
     
         if (project == null) {
             throw new ResolutionException("No such project exists!");
         } else {
             
             return ResponseEntity.ok(project);
         }
     }



     @PutMapping("/{id}")
     public ResponseEntity<JsonResponse> updateProject(@RequestBody ProjectDTO projectDTO, @PathVariable long id){

            
            
            
               projectService.UpdateProject(projectDTO,id);
               return  new ResponseEntity<JsonResponse>(
                  new JsonResponse(201, "Project has  been Updated successfully!")
               , HttpStatus.OK);
            
       
      
     }
 
    


}
