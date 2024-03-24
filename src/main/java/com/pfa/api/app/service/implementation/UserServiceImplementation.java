package com.pfa.api.app.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pfa.api.app.dto.UserDTO;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.service.UserService;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
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
}
