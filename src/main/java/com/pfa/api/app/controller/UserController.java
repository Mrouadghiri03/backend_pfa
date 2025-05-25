package com.pfa.api.app.controller;

import java.util.List;

import com.pfa.api.app.auth.RegisterDTO;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.security.JwtService;
import com.pfa.api.app.util.UserUtils;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pfa.api.app.dto.requests.UserDTO;
import com.pfa.api.app.dto.responses.UserResponseDTO;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final  UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/{userId}/uploadProfileImage")
    public ResponseEntity<UserResponseDTO> uploadProfileImage(@PathVariable Long userId,
            @RequestParam("image") MultipartFile imageFile) {
        try {
            System.out.println("Received image file: " + imageFile.getOriginalFilename());
            UserResponseDTO updatedUser = userService.uploadProfileImage(userId, imageFile);
            System.out.println("Profile image uploaded successfully for user ID: " + userId);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            System.err.println("Error uploading profile image for user ID: " + userId);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{userId}/downloadProfileImage")
    public ResponseEntity<byte[]> downloadProfileImage(@PathVariable Long userId) {
        try {
            ResponseEntity<byte[]> image = userService.downloadProfileImage(userId);
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

   /* @PostMapping("/{userId}/uploadProfileImage")
    public ResponseEntity<UserResponseDTO> uploadProfileImage(@PathVariable Long userId,
                                                              @RequestParam("image") MultipartFile imageFile) {
        try {
            System.out.println("Received image file: " + imageFile.getOriginalFilename());
            UserResponseDTO updatedUser = userService.uploadProfileImage(userId, imageFile);
            System.out.println("Profile image uploaded successfully for user ID: " + userId);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            System.err.println("Error uploading profile image for user ID: " + userId);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    */


    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/updateMyDetails")
    public ResponseEntity<UserResponseDTO> updateCurrentUser(
            @RequestBody UserDTO userDTO) throws ChangeSetPersister.NotFoundException {

        // 1. Récupérer l'utilisateur connecté via UserUtils
        User currentUser = UserUtils.getCurrentUser(userRepository);

        // 2. Mettre à jour l'utilisateur
        UserResponseDTO updatedUser = userService.updateUser(currentUser.getId(), userDTO);

        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDto) {
        try {

            UserResponseDTO updatedUser = userService.updateUser(userId, userDto);

            System.out.println("user "+updatedUser.getLastName()+" "+updatedUser.getFirstName()+" is updated succefuly");
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/supervisors")
    public ResponseEntity<List<UserResponseDTO>> getSupervisors() {
        List<UserResponseDTO> supervisors = userService.getSupervisors();
        return ResponseEntity.ok(supervisors);
    }
    @GetMapping("/students")
    public ResponseEntity<List<UserResponseDTO>> getStudents() {
        List<UserResponseDTO> supervisors = userService.getStudents();
        return ResponseEntity.ok(supervisors);
    }
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(//ici je peux utiliser userDTP ou bien UserResponseDTO
            @RequestHeader("Authorization") String authHeader) {

        // 1. Extraire le token du header
        String token = authHeader.substring(7); // Enlève "Bearer "

        // 2. Récupérer l'email depuis le token JWT
        String userEmail = jwtService.extractUsername(token);

        // 3. Chercher l'utilisateur dans la base
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 4. Convertir en DTO et retourner
        UserDTO response = new UserDTO(
               /* user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCin(),
                user.getInscriptionNumber(),
                user.getBranch(),
                user.getRoles()

                */);
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setCin(user.getCin());
        //response.setPassword(); c'est pas la peine t3zouha ila drto fiha cher
        //c'est pas la peine car dejale mot de passe ne sera pas afficher comme l'autre data

        return ResponseEntity.ok(response);
    }

}
