package com.example.backend.repository;

import com.example.backend.model.Agenda;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
    boolean existsByAnnee(Integer annee);

    Optional<Agenda> findByAnnee(Integer annee);
}
