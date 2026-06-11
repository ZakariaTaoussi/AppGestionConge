package com.example.backend.repository;

import com.example.backend.model.Departement;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartementRepository extends JpaRepository<Departement, Long> {
    boolean existsByNom(String nom);

    Optional<Departement> findByNom(String nom);
}
