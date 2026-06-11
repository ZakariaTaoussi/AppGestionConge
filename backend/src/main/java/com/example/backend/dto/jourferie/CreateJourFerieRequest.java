package com.example.backend.dto.jourferie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateJourFerieRequest(
        @NotBlank(message = "Le nom du jour ferie est obligatoire")
        @Size(max = 100, message = "Le nom du jour ferie ne doit pas depasser 100 caracteres")
        String nom,
        @NotNull(message = "La date de debut est obligatoire")
        LocalDate dateDebut,
        @NotNull(message = "La date de fin est obligatoire")
        LocalDate dateFin,
        @Size(max = 255, message = "La description ne doit pas depasser 255 caracteres")
        String description,
        @NotNull(message = "L'agenda est obligatoire")
        Long agendaId
) {
}
