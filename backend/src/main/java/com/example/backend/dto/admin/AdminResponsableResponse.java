package com.example.backend.dto.admin;

import com.example.backend.domain.Role;

public record AdminResponsableResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        Role role
) {
}
