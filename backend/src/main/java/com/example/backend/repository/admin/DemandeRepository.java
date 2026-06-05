package com.example.backend.repository.admin;

import com.example.backend.domain.Demande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandeRepository extends JpaRepository<Demande, Long> {

    boolean existsByUtilisateurId(Long utilisateurId);
}
