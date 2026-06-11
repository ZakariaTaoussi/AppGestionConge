package com.example.backend.mapper;

import com.example.backend.dto.jourferie.JourFerieResponse;
import com.example.backend.model.JourFerie;
import org.springframework.stereotype.Component;

@Component
public class JourFerieMapper {

    public JourFerieResponse toResponse(JourFerie jourFerie) {
        return new JourFerieResponse(
                jourFerie.getId(),
                jourFerie.getNom(),
                jourFerie.getDateDebut(),
                jourFerie.getDateFin(),
                jourFerie.getDescription(),
                jourFerie.getAgenda().getId(),
                jourFerie.getAgenda().getAnnee()
        );
    }
}
