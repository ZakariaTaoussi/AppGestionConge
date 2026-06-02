package com.example.backend.dto.admin;

import java.time.LocalDate;

public record AdminJourFerieRequest(
        LocalDate dateDebut,
        LocalDate dateFin,
        String description
) {
}
