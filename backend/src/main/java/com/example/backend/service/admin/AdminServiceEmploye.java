package com.example.backend.service.admin;

import com.example.backend.domain.Departement;
import com.example.backend.domain.Email;
import com.example.backend.domain.Role;
import com.example.backend.domain.Utilisateur;
import com.example.backend.dto.admin.AdminCreateEmployeRequest;
import com.example.backend.dto.admin.AdminEmployeResponse;
import com.example.backend.dto.admin.AdminPageResponse;
import com.example.backend.dto.admin.AdminUpdateEmployeRequest;
import com.example.backend.exception.departement.DepartementNonTrouveException;
import com.example.backend.exception.utilisateur.UtilisateurDejaExisteException;
import com.example.backend.exception.utilisateur.UtilisateurNonTrouveException;
import com.example.backend.exception.utilisateur.UtilisateurUtiliseException;
import com.example.backend.repository.admin.DepartementRepository;
import com.example.backend.repository.admin.DemandeRepository;
import com.example.backend.repository.admin.SoldeCongeRepository;
import com.example.backend.repository.utilisateur.UtilisateurRepository;
import com.example.backend.repository.auth.PasswordSetupTokenRepository;
import com.example.backend.service.auth.PasswordSetupService;
import com.example.backend.service.mail.MailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceEmploye {

    private static final int MAX_PAGE_SIZE = 100;

    private final UtilisateurRepository utilisateurRepository;
    private final DepartementRepository departementRepository;
    private final DemandeRepository demandeRepository;
    private final SoldeCongeRepository soldeCongeRepository;
    private final PasswordSetupTokenRepository passwordSetupTokenRepository;
    private final PasswordSetupService passwordSetupService;
    private final MailService mailService;

    public AdminServiceEmploye(
            UtilisateurRepository utilisateurRepository,
            DepartementRepository departementRepository,
            DemandeRepository demandeRepository,
            SoldeCongeRepository soldeCongeRepository,
            PasswordSetupTokenRepository passwordSetupTokenRepository,
            PasswordSetupService passwordSetupService,
            MailService mailService
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.departementRepository = departementRepository;
        this.demandeRepository = demandeRepository;
        this.soldeCongeRepository = soldeCongeRepository;
        this.passwordSetupTokenRepository = passwordSetupTokenRepository;
        this.passwordSetupService = passwordSetupService;
        this.mailService = mailService;
    }

    @Transactional(readOnly = true)
    public AdminPageResponse<AdminEmployeResponse> getEmployes(int page, int size, String search) {
        if (page < 0) {
            throw new IllegalArgumentException("Le numero de page doit etre positif ou nul");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("La taille de page doit etre comprise entre 1 et " + MAX_PAGE_SIZE);
        }

        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "nom").and(Sort.by("prenom"))
        );
        Page<Utilisateur> employes = search == null || search.isBlank()
                ? utilisateurRepository.findAll(pageRequest)
                : utilisateurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                        search.trim(),
                        search.trim(),
                        pageRequest
                );

        return new AdminPageResponse<>(
                employes.getContent().stream()
                        .map(utilisateur -> toResponse(utilisateur, null))
                        .toList(),
                employes.getNumber(),
                employes.getSize(),
                employes.getTotalElements(),
                employes.getTotalPages()
        );
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
        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
        String setupToken = passwordSetupService.createTokenFor(savedUtilisateur);
        mailService.sendPasswordSetupEmail(savedUtilisateur, setupToken);

        return toResponse(savedUtilisateur, setupToken);
    }

    @Transactional
    public AdminEmployeResponse updateEmploye(Long id, AdminUpdateEmployeRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new UtilisateurNonTrouveException(id));

        String nom = requiredText(request.nom(), "Le nom est obligatoire");
        String prenom = requiredText(request.prenom(), "Le prenom est obligatoire");
        Email email = new Email(request.email());
        Role role = requiredRole(request.role());
        Departement departement = getDepartement(request.departementId());
        validateResponsableAssignment(utilisateur, role, departement);

        utilisateurRepository.findByEmailValue(email.getValue())
                .filter(existingUtilisateur -> !existingUtilisateur.getId().equals(id))
                .ifPresent(existingUtilisateur -> {
                    throw new UtilisateurDejaExisteException(email.getValue());
                });

        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(email);
        utilisateur.setRole(role);
        utilisateur.setDepartement(departement);

        return toResponse(utilisateur, null);
    }

    @Transactional
    public void deleteEmploye(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new UtilisateurNonTrouveException(id));

        validateCanDelete(utilisateur);
        passwordSetupTokenRepository.deleteByUtilisateurId(id);
        utilisateurRepository.delete(utilisateur);
    }

    private void validateCanDelete(Utilisateur utilisateur) {
        Long utilisateurId = utilisateur.getId();

        if (departementRepository.findByResponsableId(utilisateurId).isPresent()) {
            throw new UtilisateurUtiliseException("Impossible de supprimer cet utilisateur car il est responsable d'un departement");
        }

        if (demandeRepository.existsByUtilisateurId(utilisateurId)) {
            throw new UtilisateurUtiliseException("Impossible de supprimer cet utilisateur car il possede des demandes");
        }

        if (soldeCongeRepository.existsByUtilisateurId(utilisateurId)) {
            throw new UtilisateurUtiliseException("Impossible de supprimer cet utilisateur car il possede un solde de conge");
        }
    }

    private void validateResponsableAssignment(Utilisateur utilisateur, Role role, Departement departement) {
        departementRepository.findByResponsableId(utilisateur.getId())
                .ifPresent(responsableDepartement -> {
                    if (role != Role.RESPONSABLE || !responsableDepartement.getId().equals(departement.getId())) {
                        throw new UtilisateurUtiliseException(
                                "Cet utilisateur est responsable d'un departement. Modifiez son affectation depuis la page Departements"
                        );
                    }
                });
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

    private AdminEmployeResponse toResponse(Utilisateur utilisateur, String setupToken) {
        Departement departement = utilisateur.getDepartement();

        return new AdminEmployeResponse(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail().getValue(),
                utilisateur.getRole(),
                departement != null ? departement.getId() : null,
                departement != null ? departement.getNom() : null,
                setupToken
        );
    }
}
