package com.example.backend.service.auth;

import com.example.backend.domain.PasswordSetupToken;
import com.example.backend.domain.Utilisateur;
import com.example.backend.dto.auth.SetupPasswordRequest;
import com.example.backend.exception.auth.PasswordSetupTokenDejaUtiliseException;
import com.example.backend.exception.auth.PasswordSetupTokenExpireException;
import com.example.backend.exception.auth.PasswordSetupTokenInvalideException;
import com.example.backend.repository.auth.PasswordSetupTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PasswordSetupService {

    private static final int TOKEN_BYTES_LENGTH = 32;
    private static final int TOKEN_EXPIRATION_HOURS = 24;

    private final PasswordSetupTokenRepository passwordSetupTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordSetupService(
            PasswordSetupTokenRepository passwordSetupTokenRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.passwordSetupTokenRepository = passwordSetupTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String createTokenFor(Utilisateur utilisateur) {
        String token = generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS);

        passwordSetupTokenRepository.save(new PasswordSetupToken(token, utilisateur, expiresAt));

        return token;
    }

    @Transactional
    public void setupPassword(SetupPasswordRequest request) {
        String token = requiredText(request.token(), "Le token est obligatoire");
        String password = requiredText(request.password(), "Le mot de passe est obligatoire");

        PasswordSetupToken setupToken = passwordSetupTokenRepository.findByToken(token)
                .orElseThrow(PasswordSetupTokenInvalideException::new);

        if (setupToken.isUsed()) {
            throw new PasswordSetupTokenDejaUtiliseException();
        }

        if (setupToken.isExpired(LocalDateTime.now())) {
            throw new PasswordSetupTokenExpireException();
        }

        Utilisateur utilisateur = setupToken.getUtilisateur();
        utilisateur.setPassword(passwordEncoder.encode(password));
        setupToken.markAsUsed();
    }

    private String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_BYTES_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private String requiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
