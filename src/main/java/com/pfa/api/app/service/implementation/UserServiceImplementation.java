package com.pfa.api.app.service.implementation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pfa.api.app.dto.requests.UserDTO;
import com.pfa.api.app.dto.responses.UserResponseDTO;
import com.pfa.api.app.entity.user.RoleName;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.security.JwtService;
import com.pfa.api.app.service.UserService;
import com.pfa.api.app.util.FileUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${upload.directory.photos}")
    private String USER_IMAGES_DIRECTORY;

    @Override
    public UserResponseDTO uploadProfileImage(Long userId, MultipartFile imageFile) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (!imageFile.isEmpty()) {
            FileUtils.saveUserImage(imageFile,user,USER_IMAGES_DIRECTORY,userRepository);
        }

        return UserResponseDTO.fromEntity(user);
    }

    @Override
    public UserResponseDTO getUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
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
    public UserResponseDTO updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (userDTO.getFirstName() != null && !userDTO.getFirstName().isEmpty() &&
                !userDTO.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null && !userDTO.getLastName().isEmpty() &&
                !userDTO.getLastName().equals(user.getLastName())) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty() &&
                !userDTO.getEmail().equals(user.getEmail())) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
            if (!encodedPassword.equals(user.getPassword())) {
                user.setPassword(encodedPassword);
            }
        }
        if (userDTO.getInscriptionNumber() != null && !userDTO.getInscriptionNumber().isEmpty() &&
                !userDTO.getInscriptionNumber().equals(user.getInscriptionNumber())) {
            user.setInscriptionNumber(userDTO.getInscriptionNumber());
        }

        user = userRepository.save(user);

        // Générer un nouveau token avec les nouvelles informations de l'utilisateur
        String newToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), new ArrayList<>()));

        UserResponseDTO responseDTO = UserResponseDTO.fromEntity(user);
        responseDTO.setToken(newToken);

        return responseDTO;
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

    @Override
    public ResponseEntity<byte[]> downloadProfileImage(Long userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return FileUtils.downloadUserImage(user, USER_IMAGES_DIRECTORY);
    }

}
