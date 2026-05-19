package com.example.backend.mapper.utilisateur;

import com.example.backend.domain.Utilisateur;
import com.example.backend.dto.utilisateur.UtilisateurResponse;

public final class UtilisateurMapper {

    private UtilisateurMapper() {
    }

    public static UtilisateurResponse toResponse(Utilisateur utilisateur) {
        return new UtilisateurResponse(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail().getValue(),
                utilisateur.getRole()
        );
    }
}
