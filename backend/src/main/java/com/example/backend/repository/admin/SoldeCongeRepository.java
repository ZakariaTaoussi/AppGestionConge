package com.example.backend.repository.admin;

import com.example.backend.domain.SoldeConge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoldeCongeRepository extends JpaRepository<SoldeConge, Long> {

    boolean existsByUtilisateurId(Long utilisateurId);
}
