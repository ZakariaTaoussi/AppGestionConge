package com.example.backend.mapper;

import com.example.backend.dto.agenda.AgendaResponse;
import com.example.backend.model.Agenda;
import org.springframework.stereotype.Component;

@Component
public class AgendaMapper {

    public AgendaResponse toResponse(Agenda agenda) {
        return new AgendaResponse(agenda.getId(), agenda.getAnnee());
    }
}
