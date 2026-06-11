package com.example.backend.dto.agenda;

import jakarta.validation.constraints.NotNull;

public record CreateAgendaRequest(
        @NotNull(message = "L'annee est obligatoire")
        Integer annee
) {
}
