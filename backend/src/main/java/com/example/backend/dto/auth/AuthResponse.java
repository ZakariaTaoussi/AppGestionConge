package com.example.backend.dto.auth;

import com.example.backend.model.enums.Role;

public record AuthResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        Role role,
        String message
) {
}
