package com.pfa.api.app.auth;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.pfa.api.app.JsonRsponse.JsonResponse;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    
    @PostMapping("/register")
    public ResponseEntity<JsonResponse> register(@RequestBody RegisterDTO request) throws SQLIntegrityConstraintViolationException{
        // using this part when i want that confirmation stuff
        authenticationService.register(request);
        return new ResponseEntity<JsonResponse>(new JsonResponse(201,
                "Account registered successfully , Your request will be sent to the head of the branch you're in to check you're credentials , you will get an email informing you about you're account's state."),
                HttpStatus.CREATED);

        // return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationDTO request)throws JwtException{
        System.out.println(request);
        return ResponseEntity.ok(authenticationService.authenticate(request));

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

    @GetMapping("/accept")
    public ModelAndView acceptUserAccount(@RequestParam("token") String token){
        authenticationService.acceptUser(token);
        ModelAndView modelAndView = new ModelAndView();

        // return new ResponseEntity<JsonResponse>(new JsonResponse(200, "you account has been confirmed"), HttpStatus.OK);
        modelAndView.setViewName("accept-user"); 
        modelAndView.setStatus(HttpStatus.OK);
        
        return modelAndView;

    }

    @GetMapping("/reject")
    public ModelAndView rejectUserAccount(@RequestParam("token") String token){
        authenticationService.rejectUser(token);
        ModelAndView modelAndView = new ModelAndView();

        // return new ResponseEntity<JsonResponse>(new JsonResponse(200, "you account has been confirmed"), HttpStatus.OK);
        modelAndView.setViewName("reject-user"); 
        modelAndView.setStatus(HttpStatus.OK);
        
        return modelAndView;

    }
}
