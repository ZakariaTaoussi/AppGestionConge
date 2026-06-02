package com.example.backend.service.admin;

import com.example.backend.domain.Departement;
import com.example.backend.domain.Utilisateur;
import com.example.backend.dto.admin.AdminProfilResponse;
import com.example.backend.exception.utilisateur.UtilisateurNonTrouveException;
import com.example.backend.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private final UtilisateurRepository utilisateurRepository;

    public AdminService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional(readOnly = true)
    public AdminProfilResponse getProfil(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmailValue(email)
                .orElseThrow(() -> new UtilisateurNonTrouveException(email));

        Departement departement = utilisateur.getDepartement();

        return new AdminProfilResponse(
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getRole(),
                departement != null ? departement.getNom() : null,
                utilisateur.getEmail().getValue(),
                responsableNomComplet(departement)
        );
    }

    private String responsableNomComplet(Departement departement) {
        if (departement == null || departement.getResponsable() == null) {
            return null;
        }

        Utilisateur responsable = departement.getResponsable();
        return responsable.getPrenom() + " " + responsable.getNom();
    }

}
