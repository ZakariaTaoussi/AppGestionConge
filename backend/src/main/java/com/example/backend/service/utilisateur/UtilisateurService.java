package com.example.backend.service.utilisateur;

import com.example.backend.domain.Email;
import com.example.backend.domain.Role;
import com.example.backend.domain.Utilisateur;
import com.example.backend.dto.utilisateur.CreateUtilisateurRequest;
import com.example.backend.dto.utilisateur.UtilisateurResponse;
import com.example.backend.exception.utilisateur.UtilisateurDejaExisteException;
import com.example.backend.mapper.utilisateur.UtilisateurMapper;
import com.example.backend.repository.utilisateur.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UtilisateurResponse creer(CreateUtilisateurRequest request) {
        String nom = requiredText(request.nom(), "Le nom est obligatoire");
        String prenom = requiredText(request.prenom(), "Le prenom est obligatoire");
        Email email = new Email(request.email());
        String password = encodePasswordIfPresent(request.password());
        Role role = requiredRole(request.role());

        utilisateurRepository.findByEmailValue(email.getValue())
                .ifPresent(utilisateur -> {
                    throw new UtilisateurDejaExisteException(email.getValue());
                });

        Utilisateur utilisateur = new Utilisateur(nom, prenom, email, password, role);
        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);

        return UtilisateurMapper.toResponse(savedUtilisateur);
    }

    @Transactional(readOnly = true)
    public List<UtilisateurResponse> lister() {
        return utilisateurRepository.findAll()
                .stream()
                .map(UtilisateurMapper::toResponse)
                .toList();
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

    private String encodePasswordIfPresent(String password) {
        if (password == null || password.isBlank()) {
            return null;
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caracteres");
        }
        return passwordEncoder.encode(password);
    }
}
