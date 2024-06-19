package com.pfa.api.app.service.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.pfa.api.app.entity.user.Role;
import com.pfa.api.app.entity.user.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfa.api.app.dto.requests.UserDTO;
import com.pfa.api.app.dto.responses.UserResponseDTO;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.service.UserService;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserResponseDTO getUser(Long userId) {
        User user =  userRepository.findById(userId).orElse(null);
        return UserResponseDTO.fromEntity(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDTO> userResponseDTOs = new ArrayList<>();
        for (User user : users) {
            UserResponseDTO userResponseDTO = UserResponseDTO.fromEntity(user);
            userResponseDTOs.add(userResponseDTO);
        }
        return userResponseDTOs;
    }

    @Override
    public User updateUser(Long userId, UserDTO userDTO) {
        return null;
    }

    @Override
    public List<User> getUsersByBranch(Long branch) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUsersByBranch'");
    }

    @Override
    public List<UserResponseDTO> getSupervisors() {
        List<User> supervisors = userRepository.findByRoleName(RoleName.ROLE_SUPERVISOR.name());
        return supervisors.stream().map(UserResponseDTO::fromEntity).collect(Collectors.toList());
    }

}
