package com.example.backend.dto.admin;

import com.example.backend.domain.Role;

public record AdminEmployeResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        Role role,
        String departement,
        String setupToken
) {
}
