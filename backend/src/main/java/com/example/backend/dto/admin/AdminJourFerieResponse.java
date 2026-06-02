package com.example.backend.dto.admin;

import java.time.LocalDate;

public record AdminJourFerieResponse(
        Long id,
        LocalDate dateDebut,
        LocalDate dateFin,
        String description
) {
}
