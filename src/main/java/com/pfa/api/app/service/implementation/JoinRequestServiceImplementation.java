package com.pfa.api.app.service.implementation;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.pfa.api.app.entity.Branch;
import com.pfa.api.app.entity.JoinRequest;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.JoinRequestRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.service.JoinRequestService;
import com.pfa.api.app.util.UserUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class JoinRequestServiceImplementation implements JoinRequestService{

    private final UserRepository userRepository;
    private final JoinRequestRepository joinRequestRepository;

    @Override
    public List<JoinRequest> getAllJoinRequests() throws NotFoundException {
        User currentUser = UserUtils.getCurrentUser(userRepository);
        Branch branch = currentUser.getBranch();
        List<JoinRequest> joinRequests = joinRequestRepository.findAllByBranch(branch);
        if(joinRequests.isEmpty()) {
            throw new NotFoundException();
        }
        return joinRequests;

    }

    @Override
    public JoinRequest getJoinRequestById(Long id) throws NotFoundException {
        return joinRequestRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public void acceptJoinRequest(Long id) throws NotFoundException {
        JoinRequest joinRequest = getJoinRequestById(id);
        joinRequest.getUser().setEnabled(true);
        joinRequest.getUser().setJoinRequest(null);
        userRepository.save(joinRequest.getUser());
        joinRequestRepository.delete(joinRequest);
        
    }
    public void rejectJoinRequest(Long id) throws NotFoundException {
        JoinRequest joinRequest = getJoinRequestById(id);
        joinRequest.getUser().setJoinRequest(null);
        joinRequestRepository.delete(joinRequest);
    }
    
}
