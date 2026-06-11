package com.example.backend.dto.jourferie;

import java.time.LocalDate;

public record JourFerieResponse(
        Long id,
        String nom,
        LocalDate dateDebut,
        LocalDate dateFin,
        String description,
        Long agendaId,
        Integer annee
) {
}
