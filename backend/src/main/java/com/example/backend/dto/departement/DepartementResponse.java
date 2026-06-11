package com.example.backend.dto.departement;

public record DepartementResponse(
        Long id,
        String nom,
        Long responsableId,
        String responsableNom,
        String responsablePrenom
) {
}
