package com.pfa.api.app.auth;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

import org.hibernate.PropertyValueException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pfa.api.app.entity.Branch;
import com.pfa.api.app.entity.JoinRequest;
import com.pfa.api.app.entity.user.Confirmation;
import com.pfa.api.app.entity.user.Role;
import com.pfa.api.app.entity.user.RoleName;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.BranchRepository;
import com.pfa.api.app.repository.ConfirmationRepository;
import com.pfa.api.app.repository.JoinRequestRepository;
import com.pfa.api.app.repository.RoleRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.security.JwtService;
import com.pfa.api.app.service.EmailService;
import com.pfa.api.app.util.FileUtils;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationRepository confirmationRepository;
    private final BranchRepository branchRepository;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final JoinRequestRepository joinRequestRepository;


    @Value("${upload.directory.photos}")
    private String USER_IMAGES_DIRECTORY;


    
    public AuthenticationResponse register(RegisterDTO request,MultipartFile image) throws SQLIntegrityConstraintViolationException ,
            PropertyValueException, NotFoundException {
                


        userRepository.findByEmail(request.getEmail())
                .ifPresent(existingUser -> {            
                    throw new RuntimeException("Email already in use.");
                });
        if (request.getCin() != null) {
            userRepository.findByCin(request.getCin())
                    .ifPresent(existingUser -> {
                        throw new RuntimeException("CIN already in use.");
                    });
        }
        if (request.getInscriptionNumber() != null) {
            
            userRepository.findByInscriptionNumber(request.getInscriptionNumber())
                    .ifPresent(existingUser -> {
                        throw new RuntimeException("Inscription Number already in use.");
                    });
            
        }

        Role userRole = roleRepository.findByName(request.getRole())
                .orElseGet(() -> roleRepository.save(new Role(request.getRole())));


        User user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .roles(new ArrayList<>())
            .cin(request.getCin())
            .inscriptionNumber(request.getInscriptionNumber())
            .enabled(false)//enabled(true)--->just to try with a deactivated acc=the acc that has been just created is by defaultt enabled
                .passwordChanged(false) // i a Ajoute ce champ
                .build();

        user.getRoles().add(userRole);

        Branch branch = new Branch();
        if (request.getRole().equals(RoleName.ROLE_STUDENT.name())) {
            branch = branchRepository.findById(request.getBranch()).orElseThrow(NotFoundException::new);
            user.setStudiedBranch(branch);
        } else if(request.getRole().equals(RoleName.ROLE_SUPERVISOR.name())) {
            branch = branchRepository.findById(request.getBranch()).orElseThrow(NotFoundException::new);
            branch.getProfs().add(user);
            user.setBranch(branch);
        } else {
            branch = branchRepository.findById(request.getBranch()).orElseThrow(NotFoundException::new);
            user.setBranch(branch);
        }
        
        userRepository.save(user);
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setUser(user);
        joinRequest.setBranch(branch);
        joinRequest.setRequestDate(new Date());
        
        joinRequestRepository.save(joinRequest);
        user.setJoinRequest(joinRequest);
        userRepository.save(user);
        if (image != null && !image.isEmpty()) {
            FileUtils.saveUserImage(image, user, USER_IMAGES_DIRECTORY, userRepository);
        }

        User headOfBranch = branch.getHeadOfBranch();
        

        // using this part when i want that confirmation stuff
        Confirmation confirmation = new Confirmation(user);
        confirmationRepository.save(confirmation);
        emailService.sendNotificationEmailToHeadOfBranch(headOfBranch , user ,confirmation.getToken());

        
        String jwtToken = jwtService.generateToken(user);


        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse registerSuperVisorOrStudentViaHeadOfBranch(
            RegisterDTO request, MultipartFile image) throws
            SQLIntegrityConstraintViolationException, PropertyValueException, NotFoundException {

        // Vérification des doublons
        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> { throw new RuntimeException("Email already in use"); });

        if (request.getCin() != null) {
            userRepository.findByCin(request.getCin())
                    .ifPresent(u -> { throw new RuntimeException("CIN already in use"); });
        }

        if (request.getInscriptionNumber() != null) {
            userRepository.findByInscriptionNumber(request.getInscriptionNumber())
                    .ifPresent(u -> { throw new RuntimeException("Inscription number in use"); });
        }

        // Gestion du rôle
        Role userRole = roleRepository.findByName(request.getRole())
                .orElseGet(() -> roleRepository.save(new Role(request.getRole())));
        String password="Ensao25";//le mot de pass par default creer pour les user creéé par le chef de fillliere ; il peu le changé ici mais il faut quon le change dans lemail aussi
        //a poser la question sur monsieur bouchentouf pour le mot de passe par default utilisé
        // Création de l'utilisateur
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(password))
                .roles(new ArrayList<>())
                .cin(request.getCin())
                .inscriptionNumber(request.getInscriptionNumber())
                .enabled(true) // Ou false selon votre workflow
                .passwordChanged(false) // i a Ajoute ce champ
                .build();

        user.getRoles().add(userRole);

        // Gestion de la branche
        Branch branch = new Branch();
        if (request.getRole().equals(RoleName.ROLE_STUDENT.name())) {
            branch = branchRepository.findById(request.getBranch()).orElseThrow(NotFoundException::new);
            user.setStudiedBranch(branch);
        } else if(request.getRole().equals(RoleName.ROLE_SUPERVISOR.name())) {
            branch = branchRepository.findById(request.getBranch()).orElseThrow(NotFoundException::new);
            branch.getProfs().add(user);
            user.setBranch(branch);
        } else {
            branch = branchRepository.findById(request.getBranch()).orElseThrow(NotFoundException::new);
            user.setBranch(branch);
        }
        if (request.getRole().equals(RoleName.ROLE_STUDENT.name())) {
            user.setStudiedBranch(branch);
        } else {
            user.setBranch(branch);
            if (request.getRole().equals(RoleName.ROLE_SUPERVISOR.name())) {
                branch.getProfs().add(user);
            }
        }

        // Sauvegarde
        user = userRepository.save(user);

        // Gestion de l'image
        if (image != null && !image.isEmpty()) {
            FileUtils.saveUserImage(image, user, USER_IMAGES_DIRECTORY, userRepository);
        }
//envoi demail

        System.out.println("email "+user.getEmail());
        System.out.println("name "+user.getFirstName()+"__"+user.getLastName());
        System.out.println("confirmation "+user.getConfirmation());//ca marche pas avec la confiramtion car jai sauter la demarche ou le head of branche
        //accept la creation de son compte par lui méme car cest le chef de filliere qui s'occupe de ca
        emailService.sendInformingEmailToNewUser(user,"Your account has been created , your password is "+password+", you can login now , and you change it ");
        //emailService.sendConfirmationEmail(user.getFirstName(), user.getEmail(), user.getConfirmation().getToken());


        // Génération du token JWT
        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /*public AuthenticationResponse authenticate(AuthenticationDTO request) {
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            throw new JwtException("Authentication failed: " + e.getMessage());
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new JwtException("User not found after authentication"));

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }*/
    public AuthenticationResponse authenticate(AuthenticationDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

    // Générer le temporaryToken même si le mot de passe n'est pas changé
    String tempToken = jwtService.generatePasswordChangeToken(user);

    if (!user.isPasswordChanged()) {

        System.out.println("Temporary Token: " + tempToken); // Debug
        return AuthenticationResponse.builder()
                .temporaryToken(tempToken)
                .passwordChangeRequired(true)
                .build();
    }

    // Si le mot de passe est déjà changé
    String jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder()
            .token(jwtToken)
        .passwordChangeRequired(false)
        .build();
    }
    public void changeInitialPassword(PasswordChangeDTO dto) {
        User user = jwtService.validatePasswordChangeToken(dto.temporaryToken());


        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Mot de passe initial incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        user.setPasswordChanged(true);
        userRepository.save(user);
    }

    public  Boolean validateToken(String token)  {
        Confirmation confirmation = confirmationRepository.findByToken(token);
        Optional<User> user = userRepository.findByEmail(confirmation.getUser().getEmail());

        if (user.isPresent()) {
            User realUser = user.get();
            realUser.setEnabled(true);
            userRepository.save(realUser);
            confirmationRepository.delete(confirmation);
            return true;
        }
        return false;
    }

    public void forgotPassword(String email) {
        String normalizedEmail = email.trim().toLowerCase(); // Normalisation
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setResetCode(UUID.randomUUID().toString());
        userRepository.save(user);
        emailService.sendForgotPasswordEmail(user);
    }


    public void acceptUserByToken(String token){
        Confirmation confirmation = confirmationRepository.findByToken(token);
        Optional<User> user = userRepository.findByEmail(confirmation.getUser().getEmail());
        if (user.isPresent()) {
            emailService.sendConfirmationEmail(user.get().getFirstName(), user.get().getEmail(), token);
        }
    }
    public void acceptUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            JoinRequest joinRequest = user.get().getJoinRequest();
            joinRequest.setUser(null);
            user.get().setJoinRequest(null);
            joinRequestRepository.delete(joinRequest);
            emailService.sendConfirmationEmail(user.get().getFirstName(), user.get().getEmail(), user.get().getConfirmation().getToken());
        }
    }
    public void acceptUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            JoinRequest joinRequest = user.get().getJoinRequest();
            joinRequest.setUser(null);
            user.get().setJoinRequest(null);
            joinRequestRepository.delete(joinRequest);
            emailService.sendConfirmationEmail(user.get().getFirstName(), user.get().getEmail(), user.get().getConfirmation().getToken());
        }
    }


    public void rejectUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            emailService.sendRejectionEmail(user.get(), user.get().getConfirmation().getToken());
            confirmationRepository.delete(user.get().getConfirmation());
            JoinRequest joinRequest = user.get().getJoinRequest();
            joinRequest.setUser(null);
            user.get().setJoinRequest(null);
            joinRequestRepository.delete(joinRequest);
            userRepository.delete(user.get());
        }
    }

    public void rejectUserByToken(String token){
        Confirmation confirmation = confirmationRepository.findByToken(token);
        Optional<User> user = userRepository.findByEmail(confirmation.getUser().getEmail());
        if (user.isPresent()) {
            emailService.sendRejectionEmail(user.get(), token);
            confirmationRepository.delete(confirmation);
            JoinRequest joinRequest = user.get().getJoinRequest();
            joinRequest.setUser(null);
            user.get().setJoinRequest(null);
            joinRequestRepository.delete(joinRequest);
            userRepository.delete(user.get());
        }
    }

    public void validateResetToken(String email, String token) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            if (user.get().getResetCode().equals(token)) {
                return;
            } else {
                throw new RuntimeException("Invalid token");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

   /* public void resetPassword(String token, String password) {
        Optional<User> user = userRepository.findByResetCode(token);
        if (user.isPresent()) {
            user.get().setPassword(passwordEncoder.encode(password));
            user.get().setResetCode(null);
            userRepository.save(user.get());
        } else {
            throw new RuntimeException("User not found");
        }
    }*/
   public void resetPassword(String token, String password) {
       Optional<User> user = userRepository.findByResetCode(token);
       if (user.isPresent()) {
           user.get().setPassword(passwordEncoder.encode(password));
           user.get().setResetCode(null);
           user.get().setPasswordChanged(true);
           userRepository.save(user.get());
       } else {
           throw new RuntimeException("User not found");
       }
   }
    
}
