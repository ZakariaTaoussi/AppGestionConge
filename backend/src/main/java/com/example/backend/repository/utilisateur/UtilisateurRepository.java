package com.example.backend.repository;

import com.example.backend.domain.Utilisateur;
import com.example.backend.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmailValue(String email);

    List<Utilisateur> findAllByDepartementId(Long departementId);

    @Query("""
            select utilisateur
            from Utilisateur utilisateur
            where utilisateur.role in :roles
              and not exists (
                  select departement.id
                  from Departement departement
                  where departement.responsable = utilisateur
              )
            order by utilisateur.nom asc, utilisateur.prenom asc
            """)
    List<Utilisateur> findCandidatsResponsables(@Param("roles") Collection<Role> roles);
}
