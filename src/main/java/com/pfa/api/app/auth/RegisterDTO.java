package com.pfa.api.app.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    
    private String firstName;
    private String lastName;
    private Long studiedBranch;
    private String phoneNumber;
    private String cin;
    private String inscriptionNumber;
    private String email;
    private String password;


}
