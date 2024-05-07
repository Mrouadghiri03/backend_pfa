package com.pfa.api.app.service;

import java.util.List;

import com.pfa.api.app.dto.requests.UserDTO;
import com.pfa.api.app.dto.responses.UserResponseDTO;
import com.pfa.api.app.entity.user.User;

public interface UserService {
    UserResponseDTO getUser(Long userId);

    List<UserResponseDTO> getAllUsers();

    List<User> getUsersByBranch(Long branch);

    User updateUser(Long userId, UserDTO userDTO);

}
