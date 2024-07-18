package com.pfa.api.app.service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.pfa.api.app.dto.requests.UserDTO;
import com.pfa.api.app.dto.responses.UserResponseDTO;
import com.pfa.api.app.entity.user.User;

public interface UserService {
    UserResponseDTO getUser(Long userId);

    List<UserResponseDTO> getAllUsers();

    List<User> getUsersByBranch(Long branch);

    UserResponseDTO updateUser(Long userId, UserDTO userDTO);

    UserResponseDTO uploadProfileImage(Long userId, MultipartFile imageFile) throws IOException;

    ResponseEntity<byte[]> downloadProfileImage(Long userId) throws IOException;

    List<UserResponseDTO> getSupervisors();

    List<UserResponseDTO> getStudents();

}
