package com.example.backend.exception.mail;

public class EmailEnvoiException extends RuntimeException {

    public EmailEnvoiException(String email) {
        super("Impossible d'envoyer l'email de configuration du mot de passe a : " + email);
    }
}
