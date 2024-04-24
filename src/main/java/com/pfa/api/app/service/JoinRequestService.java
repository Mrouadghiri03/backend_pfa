package com.pfa.api.app.service;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.pfa.api.app.entity.JoinRequest;

public interface JoinRequestService {
    List<JoinRequest> getAllJoinRequests() throws NotFoundException;
    JoinRequest getJoinRequestById(Long id) throws NotFoundException;
    void acceptJoinRequest(Long id) throws NotFoundException;
    void rejectJoinRequest(Long id) throws NotFoundException;
    
}
