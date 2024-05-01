package com.pfa.api.app.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfa.api.app.JsonRsponse.JsonResponse;
import com.pfa.api.app.dto.responses.BacklogResponseDTO;

import com.pfa.api.app.entity.Backlog;
import com.pfa.api.app.service.BacklogService;

import lombok.RequiredArgsConstructor;




import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;





@RestController
@RequestMapping("/api/project/backlog")
@RequiredArgsConstructor
public class backlogController {

    private final BacklogService backlogService ;

    @PostMapping
    public ResponseEntity<JsonResponse> CreateBacklog()throws NotFoundException {
        Backlog backlog =new Backlog();
        backlogService.AddBacklog(backlog);
        return new ResponseEntity<JsonResponse>(new JsonResponse(201, "Backlog created successfully"), HttpStatus.CREATED) ;
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<BacklogResponseDTO> getBacklog(@PathVariable Long projectId) throws NotFoundException{
       BacklogResponseDTO backlogResponseDTO= backlogService.getBacklogByIdProject(projectId);
       return ResponseEntity.ok(backlogResponseDTO);

    }
   
    
}
