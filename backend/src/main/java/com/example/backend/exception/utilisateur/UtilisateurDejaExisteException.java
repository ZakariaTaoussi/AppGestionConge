package com.example.backend.exception.utilisateur;

public class UtilisateurDejaExisteException extends RuntimeException {

    public UtilisateurDejaExisteException(String email) {
        super("Un utilisateur avec l'email " + email + " existe deja");
    }
}
