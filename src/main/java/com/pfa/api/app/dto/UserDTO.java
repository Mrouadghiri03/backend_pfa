package com.pfa.api.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String cin;

    private String inscriptionNumber;

    private String password;
}
