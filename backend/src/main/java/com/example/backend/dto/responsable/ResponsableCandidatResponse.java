package com.example.backend.dto.responsable;

import com.example.backend.model.enums.Role;

public record ResponsableCandidatResponse(
        Long id,
        Long utilisateurId,
        String nom,
        String prenom,
        String email,
        Role role,
        String matricule
) {
}
