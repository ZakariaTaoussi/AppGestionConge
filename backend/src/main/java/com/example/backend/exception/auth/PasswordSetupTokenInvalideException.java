package com.example.backend.exception.auth;

public class PasswordSetupTokenInvalideException extends RuntimeException {

    public PasswordSetupTokenInvalideException() {
        super("Le lien de configuration du mot de passe est invalide");
    }
}
