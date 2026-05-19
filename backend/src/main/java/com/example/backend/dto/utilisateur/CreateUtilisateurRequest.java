package com.example.backend.dto.utilisateur;

import com.example.backend.domain.Role;

public record CreateUtilisateurRequest(
        String nom,
        String prenom,
        String email,
        Role role
) {
}
