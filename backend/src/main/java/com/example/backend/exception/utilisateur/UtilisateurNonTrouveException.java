package com.example.backend.exception.utilisateur;

public class UtilisateurNonTrouveException extends RuntimeException {

    public UtilisateurNonTrouveException(String email) {
        super("Aucun utilisateur trouve avec l'email : " + email);
    }

    public UtilisateurNonTrouveException(Long id) {
        super("Aucun utilisateur trouve avec l'identifiant : " + id);
    }
}
