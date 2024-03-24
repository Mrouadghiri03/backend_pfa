package com.pfa.api.app.service;

import java.util.List;

import com.pfa.api.app.dto.UserDTO;
import com.pfa.api.app.entity.user.User;

public interface UserService {
    User getUser(Long userId);

    List<User> getAllUsers();

    List<User> getUsersByBranch(Long branch);

    User updateUser(Long userId, UserDTO userDTO);
}
