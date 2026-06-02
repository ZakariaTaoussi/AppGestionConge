package com.example.backend.dto.admin;

import com.example.backend.domain.Role;

public record AdminProfilResponse(
        String nom,
        String prenom,
        Role role,
        String departement,
        String email,
        String responsable
) {
}
