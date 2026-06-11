package com.example.backend.repository;

import com.example.backend.model.JourCalendrier;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JourCalendrierRepository extends JpaRepository<JourCalendrier, Long> {
    List<JourCalendrier> findByAgendaId(Long agendaId);

    List<JourCalendrier> findByAgendaIdAndDateBetween(Long agendaId, LocalDate dateDebut, LocalDate dateFin);
}
