package com.pfa.api.app.service.implementation;


import java.util.List;

import org.springframework.stereotype.Service;

import com.pfa.api.app.dto.responses.BacklogResponseDTO;
// import com.pfa.api.app.dto.responses.BacklogResponseDTO;
import com.pfa.api.app.entity.Backlog;
import com.pfa.api.app.entity.UserStory;
import com.pfa.api.app.repository.BacklogRepository;
import com.pfa.api.app.repository.UserStoryRepository;
import com.pfa.api.app.service.BacklogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BacklogServiceimplementation implements BacklogService{
     private final UserStoryRepository userStoryRepository;
     private final BacklogRepository backlogRepository;
    @Override
    public Backlog  AddBacklog(Backlog backlog) {
    
        return backlogRepository.save(backlog);
       
    }
    @Override
    public BacklogResponseDTO getBacklogByIdProject(Long idProject) {
        Backlog backlog = backlogRepository.findByProjectId(idProject);
        List<UserStory> userStories = userStoryRepository.findByBacklogId(backlog.getId());

        backlog.setUserStories(userStories);

        return BacklogResponseDTO.fromEntity(backlog);
       
    }
    
}
