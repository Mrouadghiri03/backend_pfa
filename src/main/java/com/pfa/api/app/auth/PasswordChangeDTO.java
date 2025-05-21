package com.pfa.api.app.auth;

public record PasswordChangeDTO(
        String temporaryToken,  // Token temporaire reçu après première connexion
        String oldPassword,
        String newPassword      // Nouveau mot de passe choisi par l'utilisateur
) {}