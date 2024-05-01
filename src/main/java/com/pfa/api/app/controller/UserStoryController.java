package com.pfa.api.app.controller;

import org.springframework.web.bind.annotation.RestController;

import com.pfa.api.app.JsonRsponse.JsonResponse;
import com.pfa.api.app.dto.requests.ProjectDTO;
import com.pfa.api.app.dto.requests.UserStoryDTO;
import com.pfa.api.app.dto.responses.ProjectResponseDTO;
import com.pfa.api.app.dto.responses.UserStoryResponseDTO;
import com.pfa.api.app.service.UserStoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;





@RestController
@RequestMapping("/api/project/userstory")
@RequiredArgsConstructor
public class UserStoryController {
    private final UserStoryService userStoryService;
    

    @PostMapping
    public ResponseEntity<JsonResponse> CreateUserStory(@RequestBody UserStoryDTO userStoryDTO) {
        UserStoryResponseDTO userStoryResponseDTO = userStoryService.addUserStory(userStoryDTO);
       return new ResponseEntity<JsonResponse>(new JsonResponse(201, "UserStory created successfully"), HttpStatus.CREATED) ;
        
        
    }

    @GetMapping
    public ResponseEntity<List<UserStoryResponseDTO>> getUserStories()throws NotFoundException {
           List<UserStoryResponseDTO> userStoryResponseDTOs =userStoryService.getAllUserStory();
           return ResponseEntity.ok(userStoryResponseDTOs);
    
    }

    @GetMapping("/{id}")
    public  ResponseEntity<UserStoryResponseDTO> getUserStorieById(@PathVariable Long id)throws NotFoundException{
        UserStoryResponseDTO userStoryResponseDTO = userStoryService.getUserStory(id);
        return ResponseEntity.ok(userStoryResponseDTO);
    }
    @PutMapping("/{id}")
    public ResponseEntity<JsonResponse> updateUserStory(@PathVariable Long id, @RequestBody UserStoryDTO userStoryDTO) {
          UserStoryResponseDTO userStoryResponseDTO = userStoryService.updateUserStory(userStoryDTO, id);
        
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "UserStory has been updated"), HttpStatus.ACCEPTED);
    }

   @DeleteMapping("/{id}")
   public ResponseEntity<JsonResponse> deleteUserStory(@PathVariable Long id ) {
    UserStoryResponseDTO userStoryResponseDTO = userStoryService.deleteUserStory(id);
  
  return new ResponseEntity<JsonResponse>(new JsonResponse(200, "UserStory has been Deleted"), HttpStatus.ACCEPTED);
}
    
    
    
}
