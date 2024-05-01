package com.pfa.api.app.service.implementation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.pfa.api.app.dto.requests.UserStoryDTO;
import com.pfa.api.app.dto.responses.UserResponseDTO;
import com.pfa.api.app.dto.responses.UserStoryResponseDTO;
import com.pfa.api.app.entity.Backlog;
import com.pfa.api.app.entity.Sprint;
import com.pfa.api.app.entity.UserStory;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.BacklogRepository;
import com.pfa.api.app.repository.SprintRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.repository.UserStoryRepository;
import com.pfa.api.app.service.UserStoryService;

import lombok.RequiredArgsConstructor;





@Service
@RequiredArgsConstructor
public class UserStoryImplementation implements UserStoryService {
    private final UserStoryRepository userStoryRepository;
    private final BacklogRepository backlogRepository;
    private final UserRepository userRepository;
    private final SprintRepository sprintRepository;
    
    @Override
    public UserStoryResponseDTO addUserStory(UserStoryDTO userStoryDTO) {
        User user = null;
        Sprint sprint = null;
        if (userStoryDTO.getDeveloperId() != null) {
           
            user = userRepository.findById(userStoryDTO.getDeveloperId()).get();
            
        }
        if (userStoryDTO.getSprintId() != null) {
           
            sprint = sprintRepository.findById(userStoryDTO.getSprintId()).get();
            
        }
            Backlog backlog = backlogRepository.findById(userStoryDTO.getBacklogId()).get();
            
             UserStory userStory =UserStory.builder()
                               .name(userStoryDTO.getName())
                               .description(userStoryDTO.getDescription())
                               .priority(userStoryDTO.getPriorite())
                               .story_points(userStoryDTO.getStory_points())
                               .status(userStoryDTO.getStatus())
                               .backlog(backlog)
                               .developer(user)
                               .sprint(sprint)
                               .build();

            
         UserStory savUserStory =  userStoryRepository.save(userStory);
        return UserStoryResponseDTO.fromEntity(savUserStory);
    }

    @Override
    public UserStoryResponseDTO deleteUserStory(Long id) {
        UserStory userStory =userStoryRepository.findById(id).get();
        userStoryRepository.delete(userStory);
        return UserStoryResponseDTO.fromEntity(userStory) ;
    }

    @Override
    public List<UserStoryResponseDTO>  getAllUserStory() {
        List<UserStory> userStory = userStoryRepository.findAll();
        return userStory.stream()
                .map(UserStoryResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public UserStoryResponseDTO getUserStory(Long id) {
        UserStory userStory = userStoryRepository.findById(id).get();
        return UserStoryResponseDTO.fromEntity(userStory);
    }

    @Override
    public UserStoryResponseDTO updateUserStory(UserStoryDTO userStoryDTO ,Long id) {
               UserStory userStory = userStoryRepository.findById(id).get();

               if (userStoryDTO.getName()!=null) {
                  userStory.setName(userStoryDTO.getName());
               }
               if(userStoryDTO.getDescription()!=null){
                   userStory.setDescription(userStoryDTO.getDescription());
               }
               if (userStoryDTO.getStatus()!=null) {
                   userStory.setStatus(userStoryDTO.getStatus());
               }
               if(userStoryDTO.getStory_points()!=0){
                    userStory.setStory_points(userStoryDTO.getStory_points());
               }
               if (userStoryDTO.getPriorite()!=0) {
                userStory.setPriority(userStoryDTO.getPriorite());;
                
               }

             userStoryRepository.save(userStory);  
              
        return UserStoryResponseDTO.fromEntity(userStory);
    }
  
    @Override
    public UserStoryResponseDTO  AffectUserStory(Long id,Long developId,Long sprintID){
        UserStory userStory = userStoryRepository.findById(id).get();
        
  return null;
   }
    
}


