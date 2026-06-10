package com.example.backend.service.implementations;

import com.example.backend.dto.utilisateur.CreateUtilisateurRequest;
import com.example.backend.dto.utilisateur.UtilisateurResponse;
import com.example.backend.exception.EmailAlreadyUsedException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.UtilisateurMapper;
import com.example.backend.model.Utilisateur;
import com.example.backend.repository.UtilisateurRepository;
import com.example.backend.service.interfaces.IUtilisateurService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilisateurServiceImpl implements IUtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final UtilisateurMapper utilisateurMapper;

    @Override
    public UtilisateurResponse creerUtilisateur(CreateUtilisateurRequest request) {
        if (utilisateurRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyUsedException("Email deja utilise");
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.nom())
                .prenom(request.prenom())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        return utilisateurMapper.toResponse(utilisateurRepository.save(utilisateur));
    }

    @Override
    public List<UtilisateurResponse> getAllUtilisateurs() {
        return utilisateurRepository.findAll()
                .stream()
                .map(utilisateurMapper::toResponse)
                .toList();
    }

    @Override
    public UtilisateurResponse getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id)
                .map(utilisateurMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouve"));
    }
}
