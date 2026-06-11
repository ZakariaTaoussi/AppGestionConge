package com.example.backend.service.implementations;

import com.example.backend.dto.agenda.AgendaResponse;
import com.example.backend.dto.agenda.CreateAgendaRequest;
import com.example.backend.dto.agenda.JourCalendrierResponse;
import com.example.backend.exception.BusinessRuleException;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.AgendaMapper;
import com.example.backend.mapper.JourCalendrierMapper;
import com.example.backend.model.Agenda;
import com.example.backend.model.JourCalendrier;
import com.example.backend.repository.AgendaRepository;
import com.example.backend.repository.JourCalendrierRepository;
import com.example.backend.service.interfaces.IGestionAgendaService;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GestionAgendaServiceImpl implements IGestionAgendaService {

    private final AgendaRepository agendaRepository;
    private final JourCalendrierRepository jourCalendrierRepository;
    private final AgendaMapper agendaMapper;
    private final JourCalendrierMapper jourCalendrierMapper;

    @Override
    public AgendaResponse creerAgenda(CreateAgendaRequest request) {
        Integer annee = request.annee();
        if (annee < Year.now().getValue()) {
            throw new BusinessRuleException("L'annee doit etre superieure ou egale a l'annee actuelle");
        }
        if (agendaRepository.existsByAnnee(annee)) {
            throw new ConflictException("Cette annee a deja ete creee");
        }

        Agenda agenda = Agenda.builder().annee(annee).build();
        LocalDate date = LocalDate.of(annee, 1, 1);
        LocalDate fin = LocalDate.of(annee, 12, 31);
        while (!date.isAfter(fin)) {
            agenda.getJoursCalendrier().add(JourCalendrier.builder()
                    .date(date)
                    .agenda(agenda)
                    .build());
            date = date.plusDays(1);
        }
        return agendaMapper.toResponse(agendaRepository.save(agenda));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgendaResponse> getAllAgendas() {
        return agendaRepository.findAll().stream()
                .map(agendaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AgendaResponse getAgendaById(Long id) {
        return agendaMapper.toResponse(getAgenda(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JourCalendrierResponse> getJoursCalendrier(Long agendaId) {
        if (!agendaRepository.existsById(agendaId)) {
            throw new ResourceNotFoundException("Agenda introuvable");
        }
        return jourCalendrierRepository.findByAgendaId(agendaId).stream()
                .map(jourCalendrierMapper::toResponse)
                .toList();
    }

    @Override
    public void supprimerAgenda(Long id) {
        agendaRepository.delete(getAgenda(id));
    }

    private Agenda getAgenda(Long id) {
        return agendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agenda introuvable"));
    }
}
