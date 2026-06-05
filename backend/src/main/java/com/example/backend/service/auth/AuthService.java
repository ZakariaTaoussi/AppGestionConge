package com.example.backend.service.auth;

import com.example.backend.domain.Email;
import com.example.backend.domain.Utilisateur;
import com.example.backend.dto.auth.LoginRequest;
import com.example.backend.dto.auth.LoginResponse;
import com.example.backend.exception.auth.IdentifiantsInvalidesException;
import com.example.backend.mapper.utilisateur.UtilisateurMapper;
import com.example.backend.repository.utilisateur.UtilisateurRepository;
import com.example.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UtilisateurRepository utilisateurRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResult login(LoginRequest request) {
        String email = new Email(request.email()).getValue();
        String password = requiredPassword(request.password());

        Utilisateur utilisateur = utilisateurRepository.findByEmailValue(email)
                .orElseThrow(IdentifiantsInvalidesException::new);

        if (utilisateur.getPassword() == null || !passwordEncoder.matches(password, utilisateur.getPassword())) {
            throw new IdentifiantsInvalidesException();
        }

        String token = jwtService.generateToken(utilisateur);
        LoginResponse response = new LoginResponse(UtilisateurMapper.toResponse(utilisateur));

        return new LoginResult(token, response);
    }

    private String requiredPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IdentifiantsInvalidesException();
        }
        return password;
    }

    public record LoginResult(
            String token,
            LoginResponse response
    ) {
    }
}
