package com.example.backend.dto.departement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDepartementRequest(
        @NotBlank(message = "Le nom du departement est obligatoire")
        @Size(max = 100, message = "Le nom du departement ne doit pas depasser 100 caracteres")
        String nom,
        Long responsableId
) {
}
