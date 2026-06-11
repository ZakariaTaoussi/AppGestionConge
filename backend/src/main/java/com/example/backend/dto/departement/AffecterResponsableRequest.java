package com.example.backend.dto.departement;

import jakarta.validation.constraints.NotNull;

public record AffecterResponsableRequest(
        @NotNull(message = "Le responsable est obligatoire")
        Long responsableId
) {
}
