package com.example.backend.dto.admin;

import com.example.backend.domain.Role;

public record AdminCreateEmployeRequest(
        String nom,
        String prenom,
        String email,
        Role role,
        Long departementId
) {
}
