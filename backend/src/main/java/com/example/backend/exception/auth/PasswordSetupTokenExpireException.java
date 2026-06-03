package com.example.backend.exception.auth;

public class PasswordSetupTokenExpireException extends RuntimeException {

    public PasswordSetupTokenExpireException() {
        super("Le lien de configuration du mot de passe a expire");
    }
}
