package com.example.backend.mapper;

import com.example.backend.dto.agenda.JourCalendrierResponse;
import com.example.backend.model.JourCalendrier;
import com.example.backend.model.JourFerie;
import org.springframework.stereotype.Component;

@Component
public class JourCalendrierMapper {

    public JourCalendrierResponse toResponse(JourCalendrier jourCalendrier) {
        JourFerie jourFerie = jourCalendrier.getJourFerie();
        return new JourCalendrierResponse(
                jourCalendrier.getId(),
                jourCalendrier.getDate(),
                jourCalendrier.getAgenda().getId(),
                jourFerie != null ? jourFerie.getId() : null,
                jourFerie != null ? jourFerie.getNom() : null
        );
    }
}
