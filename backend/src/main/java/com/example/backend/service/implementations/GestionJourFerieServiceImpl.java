package com.example.backend.service.implementations;

import com.example.backend.dto.jourferie.CreateJourFerieRequest;
import com.example.backend.dto.jourferie.CreateMultipleJoursFeriesRequest;
import com.example.backend.dto.jourferie.JourFerieResponse;
import com.example.backend.exception.BusinessRuleException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.JourFerieMapper;
import com.example.backend.model.Agenda;
import com.example.backend.model.JourCalendrier;
import com.example.backend.model.JourFerie;
import com.example.backend.repository.AgendaRepository;
import com.example.backend.repository.JourCalendrierRepository;
import com.example.backend.repository.JourFerieRepository;
import com.example.backend.service.interfaces.IGestionJourFerieService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GestionJourFerieServiceImpl implements IGestionJourFerieService {

    private final JourFerieRepository jourFerieRepository;
    private final JourCalendrierRepository jourCalendrierRepository;
    private final AgendaRepository agendaRepository;
    private final JourFerieMapper jourFerieMapper;

    @Override
    public JourFerieResponse creerJourFerie(CreateJourFerieRequest request) {
        validerDates(request);
        Agenda agenda = getAgenda(request.agendaId());
        validerDatesDansAgenda(request, agenda);
        JourFerie jourFerie = JourFerie.builder()
                .nom(request.nom().trim())
                .dateDebut(request.dateDebut())
                .dateFin(request.dateFin())
                .description(normalizeDescription(request.description()))
                .agenda(agenda)
                .build();
        JourFerie saved = jourFerieRepository.save(jourFerie);
        lierJoursCalendrier(saved);
        return jourFerieMapper.toResponse(saved);
    }

    @Override
    public List<JourFerieResponse> creerPlusieursJoursFeries(CreateMultipleJoursFeriesRequest request) {
        return request.joursFeries().stream()
                .map(this::creerJourFerie)
                .toList();
    }

    @Override
    public JourFerieResponse modifierJourFerie(Long id, CreateJourFerieRequest request) {
        validerDates(request);
        JourFerie jourFerie = getJourFerie(id);
        Agenda agenda = getAgenda(request.agendaId());
        validerDatesDansAgenda(request, agenda);
        delierJoursCalendrier(jourFerie);
        jourFerie.setNom(request.nom().trim());
        jourFerie.setDateDebut(request.dateDebut());
        jourFerie.setDateFin(request.dateFin());
        jourFerie.setDescription(normalizeDescription(request.description()));
        jourFerie.setAgenda(agenda);
        JourFerie saved = jourFerieRepository.save(jourFerie);
        lierJoursCalendrier(saved);
        return jourFerieMapper.toResponse(saved);
    }

    @Override
    public void supprimerJourFerie(Long id) {
        JourFerie jourFerie = getJourFerie(id);
        delierJoursCalendrier(jourFerie);
        jourFerieRepository.delete(jourFerie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JourFerieResponse> getJoursFeriesByAgenda(Long agendaId) {
        if (!agendaRepository.existsById(agendaId)) {
            throw new ResourceNotFoundException("Agenda introuvable");
        }
        return jourFerieRepository.findByAgendaId(agendaId).stream()
                .map(jourFerieMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JourFerieResponse> getAllJoursFeries() {
        return jourFerieRepository.findAll().stream()
                .map(jourFerieMapper::toResponse)
                .toList();
    }

    private void validerDates(CreateJourFerieRequest request) {
        if (request.dateDebut().isAfter(request.dateFin())) {
            throw new BusinessRuleException("La date de debut doit etre inferieure ou egale a la date de fin");
        }
    }

    private void validerDatesDansAgenda(CreateJourFerieRequest request, Agenda agenda) {
        int annee = agenda.getAnnee();
        if (request.dateDebut().getYear() != annee || request.dateFin().getYear() != annee) {
            throw new BusinessRuleException("Les dates du jour ferie doivent appartenir a l'annee de l'agenda");
        }
    }

    private void lierJoursCalendrier(JourFerie jourFerie) {
        List<JourCalendrier> jours = jourCalendrierRepository.findByAgendaIdAndDateBetween(
                jourFerie.getAgenda().getId(),
                jourFerie.getDateDebut(),
                jourFerie.getDateFin()
        );
        if (jours.isEmpty()) {
            throw new BusinessRuleException("Les dates du jour ferie doivent appartenir a l'annee de l'agenda");
        }
        jours.forEach(jour -> jour.setJourFerie(jourFerie));
        jourCalendrierRepository.saveAll(jours);
    }

    private void delierJoursCalendrier(JourFerie jourFerie) {
        List<JourCalendrier> jours = jourCalendrierRepository.findByAgendaIdAndDateBetween(
                jourFerie.getAgenda().getId(),
                jourFerie.getDateDebut(),
                jourFerie.getDateFin()
        );
        jours.stream()
                .filter(jour -> jourFerie.equals(jour.getJourFerie()))
                .forEach(jour -> jour.setJourFerie(null));
        jourCalendrierRepository.saveAll(jours);
    }

    private Agenda getAgenda(Long agendaId) {
        return agendaRepository.findById(agendaId)
                .orElseThrow(() -> new ResourceNotFoundException("Agenda introuvable"));
    }

    private JourFerie getJourFerie(Long id) {
        return jourFerieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jour ferie introuvable"));
    }

    private String normalizeDescription(String description) {
        return description == null || description.isBlank() ? null : description.trim();
    }
}
