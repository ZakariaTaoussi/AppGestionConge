package com.example.backend.repository;

import com.example.backend.model.Responsable;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponsableRepository extends JpaRepository<Responsable, Long> {
    Optional<Responsable> findByUtilisateurId(Long utilisateurId);
}
