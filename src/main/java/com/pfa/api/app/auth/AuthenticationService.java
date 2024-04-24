package com.pfa.api.app.auth;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.PropertyValueException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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


    
    public AuthenticationResponse register(RegisterDTO request) throws SQLIntegrityConstraintViolationException ,
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
            .phoneNumber(request.getPhoneNumber())
            .enabled(true)//enabled(true)--->just to try with a deactivated acc=the acc that has been just created is by defaultt enabled
            .build();

        user.getRoles().add(userRole);

        Branch branch = new Branch();
        if (request.getRole().equals(RoleName.ROLE_STUDENT.name())) {
            branch = branchRepository.findById(request.getBranch()).orElseThrow(NotFoundException::new);
            user.setStudiedBranch(branch);
        } else if(request.getRole().equals(RoleName.ROLE_SUPERVISOR.name())) {
            branch = branchRepository.findById(request.getBranch()).orElseThrow(NotFoundException::new);
            branch.getProfs().add(user);
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

        User headOfBranch = branch.getHeadOfBranch();
        

        // using this part when i want that confirmation stuff
        // Confirmation confirmation = new Confirmation(user);
        // confirmationRepository.save(confirmation);
        // emailService.sendNotificationEmailToHeadOfBranch(headOfBranch , user ,confirmation.getToken());

        
        String jwtToken = jwtService.generateToken(user);


        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationDTO request) {
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

    // Todo latter !!!
    public  void sendPasswordResetLink(String email)  {}

    public void acceptUser(String token){
        Confirmation confirmation = confirmationRepository.findByToken(token);
        Optional<User> user = userRepository.findByEmail(confirmation.getUser().getEmail());
        if (user.isPresent()) {
            emailService.sendConfirmationEmail(user.get().getFirstName(), user.get().getEmail(), token);
            
        }
    }

    public void rejectUser(String token){
        Confirmation confirmation = confirmationRepository.findByToken(token);
        Optional<User> user = userRepository.findByEmail(confirmation.getUser().getEmail());
        if (user.isPresent()) {
            emailService.sendRejectionEmail(user.get(), token);
            confirmationRepository.delete(confirmation);
            userRepository.delete(user.get());
        }
    }
    
}
