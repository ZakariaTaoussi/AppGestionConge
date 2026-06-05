package com.example.backend.service.admin;

import com.example.backend.domain.Departement;
import com.example.backend.domain.Role;
import com.example.backend.domain.Utilisateur;
import com.example.backend.dto.admin.AdminDepartementRequest;
import com.example.backend.dto.admin.AdminDepartementResponse;
import com.example.backend.dto.admin.AdminPageResponse;
import com.example.backend.dto.admin.AdminResponsableResponse;
import com.example.backend.exception.departement.DepartementDejaExisteException;
import com.example.backend.exception.departement.DepartementNonTrouveException;
import com.example.backend.exception.departement.DepartementUtiliseException;
import com.example.backend.exception.departement.ResponsableDejaAffecteException;
import com.example.backend.exception.departement.ResponsableDepartementInvalideException;
import com.example.backend.exception.utilisateur.UtilisateurNonTrouveException;
import com.example.backend.repository.admin.DepartementRepository;
import com.example.backend.repository.utilisateur.UtilisateurRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminServiceDepartement {

    private static final int NOM_MAX_LENGTH = 100;
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<Role> ROLES_RESPONSABLE_AUTORISES = EnumSet.of(
            Role.EMPLOYE,
            Role.RH,
            Role.RESPONSABLE
    );

    private final DepartementRepository departementRepository;
    private final UtilisateurRepository utilisateurRepository;

    public AdminServiceDepartement(
            DepartementRepository departementRepository,
            UtilisateurRepository utilisateurRepository
    ) {
        this.departementRepository = departementRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional(readOnly = true)
    public AdminPageResponse<AdminDepartementResponse> getDepartements(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Le numero de page doit etre positif ou nul");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("La taille de page doit etre comprise entre 1 et " + MAX_PAGE_SIZE);
        }

        Page<Departement> departements = departementRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "nom"))
        );

        return new AdminPageResponse<>(
                departements.getContent().stream()
                        .map(this::toResponse)
                        .toList(),
                departements.getNumber(),
                departements.getSize(),
                departements.getTotalElements(),
                departements.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public List<AdminResponsableResponse> getCandidatsResponsables() {
        return utilisateurRepository.findCandidatsResponsables(ROLES_RESPONSABLE_AUTORISES).stream()
                .map(this::toResponsableResponse)
                .toList();
    }

    @Transactional
    public AdminDepartementResponse createDepartement(AdminDepartementRequest request) {
        String nom = normalizeNom(request.nom());
        if (departementRepository.existsByNomIgnoreCase(nom)) {
            throw new DepartementDejaExisteException(nom);
        }

        Utilisateur responsable = getResponsable(request.responsableId(), null);
        Departement departement = departementRepository.save(new Departement(nom, responsable));
        affecterResponsable(departement, responsable);

        return toResponse(departement);
    }

    @Transactional
    public AdminDepartementResponse updateDepartement(Long id, AdminDepartementRequest request) {
        Departement departement = getDepartement(id);
        String nom = normalizeNom(request.nom());
        if (departementRepository.existsByNomIgnoreCaseAndIdNot(nom, id)) {
            throw new DepartementDejaExisteException(nom);
        }

        Utilisateur ancienResponsable = departement.getResponsable();
        Utilisateur nouveauResponsable = getResponsable(request.responsableId(), id);

        departement.setNom(nom);
        if (!sameUtilisateur(ancienResponsable, nouveauResponsable)) {
            detacherResponsable(departement, ancienResponsable);
            departement.setResponsable(nouveauResponsable);
            affecterResponsable(departement, nouveauResponsable);
        }

        return toResponse(departementRepository.save(departement));
    }

    @Transactional
    public void deleteDepartement(Long id) {
        Departement departement = getDepartement(id);
        Utilisateur responsable = departement.getResponsable();
        boolean utilisateurAffecte = utilisateurRepository.findAllByDepartementId(id).stream()
                .anyMatch(utilisateur -> !sameUtilisateur(utilisateur, responsable));
        if (utilisateurAffecte) {
            throw new DepartementUtiliseException(id);
        }

        detacherResponsable(departement, responsable);
        departement.setResponsable(null);
        departementRepository.delete(departement);
    }

    private Utilisateur getResponsable(Long responsableId, Long departementId) {
        if (responsableId == null) {
            return null;
        }

        Utilisateur responsable = utilisateurRepository.findById(responsableId)
                .orElseThrow(() -> new UtilisateurNonTrouveException(responsableId));
        if (!ROLES_RESPONSABLE_AUTORISES.contains(responsable.getRole())) {
            throw new ResponsableDepartementInvalideException(
                    "Seuls les utilisateurs EMPLOYE, RH ou RESPONSABLE peuvent devenir responsables de departement"
            );
        }

        departementRepository.findByResponsableId(responsableId)
                .filter(departement -> !departement.getId().equals(departementId))
                .ifPresent(departement -> {
                    throw new ResponsableDejaAffecteException();
                });

        return responsable;
    }

    private void affecterResponsable(Departement departement, Utilisateur responsable) {
        if (responsable == null) {
            return;
        }

        responsable.setRole(Role.RESPONSABLE);
        responsable.setDepartement(departement);
        utilisateurRepository.save(responsable);
    }

    private void detacherResponsable(Departement departement, Utilisateur responsable) {
        if (responsable != null && responsable.getDepartement() == departement) {
            responsable.setDepartement(null);
            utilisateurRepository.save(responsable);
        }
    }

    private boolean sameUtilisateur(Utilisateur first, Utilisateur second) {
        if (first == null || second == null) {
            return first == second;
        }
        return first.getId().equals(second.getId());
    }

    private Departement getDepartement(Long id) {
        return departementRepository.findById(id)
                .orElseThrow(() -> new DepartementNonTrouveException(id));
    }

    private String normalizeNom(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom du departement est obligatoire");
        }

        String normalizedNom = nom.trim();
        if (normalizedNom.length() > NOM_MAX_LENGTH) {
            throw new IllegalArgumentException("Le nom du departement ne doit pas depasser " + NOM_MAX_LENGTH + " caracteres");
        }
        return normalizedNom;
    }

    private AdminDepartementResponse toResponse(Departement departement) {
        return new AdminDepartementResponse(
                departement.getId(),
                departement.getNom(),
                departement.getResponsable() != null
                        ? toResponsableResponse(departement.getResponsable())
                        : null
        );
    }

    private AdminResponsableResponse toResponsableResponse(Utilisateur responsable) {
        return new AdminResponsableResponse(
                responsable.getId(),
                responsable.getNom(),
                responsable.getPrenom(),
                responsable.getEmail().getValue(),
                responsable.getRole()
        );
    }
}
