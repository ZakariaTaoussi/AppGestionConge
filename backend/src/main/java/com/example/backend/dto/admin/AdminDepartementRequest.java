package com.example.backend.dto.admin;

public record AdminDepartementRequest(
        String nom,
        Long responsableId
) {
}
