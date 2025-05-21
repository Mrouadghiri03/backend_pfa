package com.pfa.api.app.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationDTO {

    private String inscriptionNumber;  // i a Remplace 'email' par 'inscriptionNumber'
    private String password;
}
