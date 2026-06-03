package com.example.backend.service.admin;

import com.example.backend.domain.Departement;
import com.example.backend.domain.Email;
import com.example.backend.domain.Role;
import com.example.backend.domain.Utilisateur;
import com.example.backend.dto.admin.AdminCreateEmployeRequest;
import com.example.backend.dto.admin.AdminEmployeResponse;
import com.example.backend.exception.departement.DepartementNonTrouveException;
import com.example.backend.exception.utilisateur.UtilisateurDejaExisteException;
import com.example.backend.repository.DepartementRepository;
import com.example.backend.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceEmploye {

    private final UtilisateurRepository utilisateurRepository;
    private final DepartementRepository departementRepository;

    public AdminServiceEmploye(
            UtilisateurRepository utilisateurRepository,
            DepartementRepository departementRepository
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.departementRepository = departementRepository;
    }

    @Transactional
    public AdminEmployeResponse createEmploye(AdminCreateEmployeRequest request) {
        String nom = requiredText(request.nom(), "Le nom est obligatoire");
        String prenom = requiredText(request.prenom(), "Le prenom est obligatoire");
        Email email = new Email(request.email());
        Role role = requiredRole(request.role());
        Departement departement = getDepartement(request.departementId());

        utilisateurRepository.findByEmailValue(email.getValue())
                .ifPresent(utilisateur -> {
                    throw new UtilisateurDejaExisteException(email.getValue());
                });

        Utilisateur utilisateur = new Utilisateur(nom, prenom, email, null, role, departement);
        return toResponse(utilisateurRepository.save(utilisateur));
    }

    private Departement getDepartement(Long departementId) {
        if (departementId == null) {
            throw new IllegalArgumentException("Le departement est obligatoire");
        }

        return departementRepository.findById(departementId)
                .orElseThrow(() -> new DepartementNonTrouveException(departementId));
    }

    private String requiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private Role requiredRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Le role est obligatoire");
        }
        return role;
    }

    private AdminEmployeResponse toResponse(Utilisateur utilisateur) {
        Departement departement = utilisateur.getDepartement();

        return new AdminEmployeResponse(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail().getValue(),
                utilisateur.getRole(),
                departement != null ? departement.getNom() : null
        );
    }
}
