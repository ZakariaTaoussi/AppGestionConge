package com.example.backend.repository.utilisateur;

import com.example.backend.domain.Role;
import com.example.backend.domain.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmailValue(String email);

    List<Utilisateur> findAllByDepartementId(Long departementId);

    @Query("""
            select u
            from Utilisateur u
            where u.role in :roles
              and not exists (
                  select d.id
                  from Departement d
                  where d.responsable = u
              )
            order by u.nom asc, u.prenom asc
            """)
    List<Utilisateur> findCandidatsResponsables(@Param("roles") Set<Role> roles);

    Page<Utilisateur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
            String nom,
            String prenom,
            Pageable pageable
    );
}
