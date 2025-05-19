package com.pfa.api.app.auth;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

import com.pfa.api.app.entity.user.RoleName;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.UserRepository;
import org.hibernate.PropertyValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.pfa.api.app.security.JwtService;

import com.pfa.api.app.JsonRsponse.JsonResponse;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    private final UserRepository userRepository;

    
  /*  @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@ModelAttribute RegisterDTO request,
                @RequestParam(value = "image", required = false) MultipartFile image) throws SQLIntegrityConstraintViolationException, PropertyValueException, NotFoundException{
        // using this part when i want that confirmation stuff
        // authenticationService.register(request);
        // return new ResponseEntity<JsonResponse>(new JsonResponse(201,
        //         "Account registered successfully , Your request will be sent to the head of the branch you're in to check you're credentials , you will get an email informing you about you're account's state."),
        //         HttpStatus.CREATED);
        
        return ResponseEntity.ok(authenticationService.register(request,image));
    }

   */
  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterDTO request) throws
          SQLIntegrityConstraintViolationException, NotFoundException {
      return ResponseEntity.ok(authenticationService.register(request, null));
  }


@PostMapping("/registerViaHoB")
public ResponseEntity<AuthenticationResponse> registerSupervisorOrStudentViaHob(
        @RequestBody RegisterDTO request,
        @RequestHeader("Authorization") String authHeader) throws
        SQLIntegrityConstraintViolationException, NotFoundException {

    // Vérification de l'authentification
    String token = authHeader.replace("Bearer ", "");// on prends ici le token
    String email = jwtService.extractUsername(token);//extraire le token
    Optional<User> currentUser = userRepository.findByEmail(email);//recupere le user connecté ; merci a yassine cteait un fonction deja implémenté par lui

    // Vérification des droits
   /* if(!currentUser.getRoles().stream().anyMatch(r ->
            r.getName().equals(RoleName.ROLE_HEAD_OF_BRANCH.name()))) {
        throw new AccessDeniedException("Only head of branch can register users");
    }
 //c ame donne des erreur ici dans le role just a ajouté les erreur dans les authorizations
    */

    return ResponseEntity.ok(authenticationService.registerSuperVisorOrStudentViaHeadOfBranch(request, null));
}

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationDTO request)throws JwtException{
        return ResponseEntity.ok(authenticationService.authenticate(request));

    }

    @PostMapping("/forgot-password")
    public ResponseEntity<JsonResponse> forgotPassword(@RequestBody String email){
        authenticationService.forgotPassword(email);
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "A security reset token has been sent to you're email"), HttpStatus.OK);
    }

    @PostMapping("/validate-token")
    public ResponseEntity<JsonResponse> validateResetToken(@RequestParam("token") String token,
            @RequestParam("email") String email){
        authenticationService.validateResetToken(email,token);
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "The token is valid"), HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<JsonResponse> resetPassword(@RequestParam("token") String token,
            @RequestParam("password") String password){
        authenticationService.resetPassword(token,password);
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "Password has been reset"), HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ModelAndView confirmUserAccount(@RequestParam("token") String token) {
        Boolean isUserConfirmed = authenticationService.validateToken(token);
        ModelAndView modelAndView = new ModelAndView();

        if (isUserConfirmed) {
            // User account is confirmed
            modelAndView.setViewName("verification-success");
            modelAndView.setStatus(HttpStatus.OK);
        } else {
            // Token is not confirmed
            modelAndView.setViewName("verification-failure");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }

        return modelAndView;
    }

    @GetMapping("/accept/token/{token}")
    public ModelAndView acceptUserAccount(@PathVariable("token") String token){
        authenticationService.acceptUserByToken(token);
        ModelAndView modelAndView = new ModelAndView();

        // return new ResponseEntity<JsonResponse>(new JsonResponse(200, "you account has been confirmed"), HttpStatus.OK);
        modelAndView.setViewName("accept-user"); 
        modelAndView.setStatus(HttpStatus.OK);
        
        return modelAndView;

    }

    @GetMapping("/reject/token/{token}")
    public ModelAndView rejectUserAccount(@PathVariable("token") String token){
        authenticationService.rejectUserByToken(token);
        ModelAndView modelAndView = new ModelAndView();

        // return new ResponseEntity<JsonResponse>(new JsonResponse(200, "you account has been confirmed"), HttpStatus.OK);
        modelAndView.setViewName("reject-user"); 
        modelAndView.setStatus(HttpStatus.OK);
        
        return modelAndView;

    }

    @PostMapping("/accept")
    // @PreAuthorize("hasRole('ROLE_HEAD_OF_BRANCH')")
    public ResponseEntity<JsonResponse> acceptUser(@RequestParam("user") Long user){
        authenticationService.acceptUser(user);
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "The user is accepted ,he can login now"), HttpStatus.OK);

    }

    @PostMapping("/reject")
    // @PreAuthorize("hasRole('ROLE_HEAD_OF_BRANCH')")
    public ResponseEntity<JsonResponse> rejectUser(@RequestParam("user") Long user){
        authenticationService.rejectUser(user);
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "The user is rejected ,he can't login now"), HttpStatus.OK);

    }
}
