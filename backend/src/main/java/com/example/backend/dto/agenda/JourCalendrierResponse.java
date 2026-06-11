package com.example.backend.dto.agenda;

import java.time.LocalDate;

public record JourCalendrierResponse(
        Long id,
        LocalDate date,
        Long agendaId,
        Long jourFerieId,
        String jourFerieNom
) {
}
