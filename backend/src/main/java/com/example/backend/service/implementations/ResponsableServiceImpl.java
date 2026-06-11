package com.example.backend.service.implementations;

import com.example.backend.dto.responsable.ResponsableCandidatResponse;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.Employe;
import com.example.backend.model.Responsable;
import com.example.backend.model.Utilisateur;
import com.example.backend.model.enums.Role;
import com.example.backend.repository.ResponsableRepository;
import com.example.backend.repository.UtilisateurRepository;
import com.example.backend.service.interfaces.IResponsableService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ResponsableServiceImpl implements IResponsableService {

    private final UtilisateurRepository utilisateurRepository;
    private final ResponsableRepository responsableRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ResponsableCandidatResponse> getCandidatsResponsables() {
        return utilisateurRepository.findByRoleIn(List.of(Role.EMPLOYE, Role.RH, Role.RESPONSABLE))
                .stream()
                .filter(this::isUtilisateurPrincipal)
                .map(this::toCandidatResponse)
                .toList();
    }

    @Override
    public Responsable convertirEnResponsable(Long employeId) {
        Utilisateur utilisateur = utilisateurRepository.findById(employeId)
                .orElseThrow(() -> new ResourceNotFoundException("Responsable introuvable"));

        if (!List.of(Role.EMPLOYE, Role.RH, Role.RESPONSABLE).contains(utilisateur.getRole())) {
            throw new ResourceNotFoundException("Responsable introuvable");
        }

        if (utilisateur.getRole() == Role.EMPLOYE) {
            utilisateur.setRole(Role.RESPONSABLE);
            utilisateurRepository.save(utilisateur);
        }

        return responsableRepository.findByUtilisateurId(utilisateur.getId())
                .orElseGet(() -> responsableRepository.save(creerProfilResponsable(utilisateur)));
    }

    private Responsable creerProfilResponsable(Utilisateur utilisateur) {
        String matricule = getMatricule(utilisateur);
        Responsable responsable = new Responsable();
        responsable.setUtilisateurId(utilisateur.getId());
        responsable.setMatricule(matricule);
        responsable.setNom(utilisateur.getNom());
        responsable.setPrenom(utilisateur.getPrenom());
        responsable.setEmail(buildResponsableEmail(utilisateur));
        responsable.setPassword(utilisateur.getPassword());
        responsable.setRole(utilisateur.getRole() == Role.EMPLOYE ? Role.RESPONSABLE : utilisateur.getRole());
        return responsable;
    }

    private boolean isUtilisateurPrincipal(Utilisateur utilisateur) {
        return !(utilisateur instanceof Employe employe && employe.getUtilisateurId() != null);
    }

    private ResponsableCandidatResponse toCandidatResponse(Utilisateur utilisateur) {
        String matricule = getMatricule(utilisateur);
        Long responsableId = responsableRepository.findByUtilisateurId(utilisateur.getId())
                .map(Responsable::getId)
                .orElse(utilisateur.getId());
        return new ResponsableCandidatResponse(
                responsableId,
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getRole(),
                matricule
        );
    }

    private String getMatricule(Utilisateur utilisateur) {
        if (utilisateur instanceof Employe employe && employe.getMatricule() != null) {
            return employe.getMatricule();
        }
        return "USR-" + utilisateur.getId();
    }

    private String buildResponsableEmail(Utilisateur utilisateur) {
        return "responsable+" + utilisateur.getId() + "." + utilisateur.getEmail();
    }
}
