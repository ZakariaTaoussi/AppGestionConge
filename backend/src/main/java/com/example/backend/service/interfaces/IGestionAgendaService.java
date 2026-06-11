package com.example.backend.service.interfaces;

import com.example.backend.dto.agenda.AgendaResponse;
import com.example.backend.dto.agenda.CreateAgendaRequest;
import com.example.backend.dto.agenda.JourCalendrierResponse;
import java.util.List;

public interface IGestionAgendaService {
    AgendaResponse creerAgenda(CreateAgendaRequest request);

    List<AgendaResponse> getAllAgendas();

    AgendaResponse getAgendaById(Long id);

    List<JourCalendrierResponse> getJoursCalendrier(Long agendaId);

    void supprimerAgenda(Long id);
}
