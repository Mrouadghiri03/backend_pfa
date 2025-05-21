package com.pfa.api.app.auth;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String token;
    private String password;
}
