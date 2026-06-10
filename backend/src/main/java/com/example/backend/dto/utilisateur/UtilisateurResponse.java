package com.example.backend.dto.utilisateur;

import com.example.backend.model.enums.Role;

public record UtilisateurResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        Role role
) {
}
