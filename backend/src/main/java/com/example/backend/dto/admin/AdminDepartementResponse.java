package com.example.backend.dto.admin;

public record AdminDepartementResponse(
        Long id,
        String nom,
        AdminResponsableResponse responsable
) {
}
