package com.example.backend.dto.departement;

import com.example.backend.model.enums.Role;

public record ResponsableResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        Role role
) {
}
