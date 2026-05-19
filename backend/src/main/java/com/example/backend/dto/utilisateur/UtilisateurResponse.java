package com.example.backend.dto.utilisateur;

import com.example.backend.domain.Role;

public record UtilisateurResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        Role role
) {
}
