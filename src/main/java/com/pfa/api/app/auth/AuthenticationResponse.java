package com.pfa.api.app.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private boolean passwordChangeRequired; // Flag de changement obligatoire
    private String temporaryToken;  // Token pour changement de mot de passe
}
