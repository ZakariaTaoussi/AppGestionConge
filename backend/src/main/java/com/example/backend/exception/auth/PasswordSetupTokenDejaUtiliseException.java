package com.example.backend.exception.auth;

public class PasswordSetupTokenDejaUtiliseException extends RuntimeException {

    public PasswordSetupTokenDejaUtiliseException() {
        super("Le lien de configuration du mot de passe a deja ete utilise");
    }
}
