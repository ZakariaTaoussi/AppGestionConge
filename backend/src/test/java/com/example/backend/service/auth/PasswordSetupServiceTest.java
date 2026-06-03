package com.example.backend.service.auth;

import com.example.backend.domain.Email;
import com.example.backend.domain.PasswordSetupToken;
import com.example.backend.domain.Role;
import com.example.backend.domain.Utilisateur;
import com.example.backend.dto.auth.SetupPasswordRequest;
import com.example.backend.exception.auth.PasswordSetupTokenDejaUtiliseException;
import com.example.backend.exception.auth.PasswordSetupTokenExpireException;
import com.example.backend.exception.auth.PasswordSetupTokenInvalideException;
import com.example.backend.repository.auth.PasswordSetupTokenRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PasswordSetupServiceTest {

    private final PasswordSetupTokenRepository passwordSetupTokenRepository = mock(PasswordSetupTokenRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final PasswordSetupService passwordSetupService = new PasswordSetupService(
            passwordSetupTokenRepository,
            passwordEncoder
    );

    @Test
    void createTokenForGenereEtSauvegardeUnTokenTemporaire() {
        Utilisateur utilisateur = new Utilisateur(
                "Benali",
                "Ahmed",
                new Email("ahmed.benali@demo.ma"),
                null,
                Role.EMPLOYE
        );

        LocalDateTime beforeCreation = LocalDateTime.now();
        String token = passwordSetupService.createTokenFor(utilisateur);

        ArgumentCaptor<PasswordSetupToken> tokenCaptor = ArgumentCaptor.forClass(PasswordSetupToken.class);
        verify(passwordSetupTokenRepository).save(tokenCaptor.capture());
        PasswordSetupToken savedToken = tokenCaptor.getValue();

        assertThat(token).isNotBlank();
        assertThat(token).isEqualTo(savedToken.getToken());
        assertThat(savedToken.getUtilisateur()).isEqualTo(utilisateur);
        assertThat(savedToken.isUsed()).isFalse();
        assertThat(savedToken.getExpiresAt()).isAfter(beforeCreation.plusHours(23));
    }

    @Test
    void setupPasswordEncodeLeMotDePasseEtMarqueLeTokenCommeUtilise() {
        Utilisateur utilisateur = new Utilisateur(
                "Benali",
                "Ahmed",
                new Email("ahmed.benali@demo.ma"),
                null,
                Role.EMPLOYE
        );
        PasswordSetupToken setupToken = new PasswordSetupToken(
                "setup-token",
                utilisateur,
                LocalDateTime.now().plusHours(1)
        );
        when(passwordSetupTokenRepository.findByToken("setup-token")).thenReturn(Optional.of(setupToken));
        when(passwordEncoder.encode("Password123")).thenReturn("encoded-password");

        passwordSetupService.setupPassword(new SetupPasswordRequest("setup-token", "Password123"));

        assertThat(utilisateur.getPassword()).isEqualTo("encoded-password");
        assertThat(setupToken.isUsed()).isTrue();
    }

    @Test
    void setupPasswordRefuseUnTokenInexistant() {
        when(passwordSetupTokenRepository.findByToken("setup-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passwordSetupService.setupPassword(
                new SetupPasswordRequest("setup-token", "Password123")
        )).isInstanceOf(PasswordSetupTokenInvalideException.class);

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void setupPasswordRefuseUnTokenExpire() {
        PasswordSetupToken setupToken = new PasswordSetupToken(
                "setup-token",
                new Utilisateur("Benali", "Ahmed", new Email("ahmed.benali@demo.ma"), null, Role.EMPLOYE),
                LocalDateTime.now().minusMinutes(1)
        );
        when(passwordSetupTokenRepository.findByToken("setup-token")).thenReturn(Optional.of(setupToken));

        assertThatThrownBy(() -> passwordSetupService.setupPassword(
                new SetupPasswordRequest("setup-token", "Password123")
        )).isInstanceOf(PasswordSetupTokenExpireException.class);

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void setupPasswordRefuseUnTokenDejaUtilise() {
        PasswordSetupToken setupToken = new PasswordSetupToken(
                "setup-token",
                new Utilisateur("Benali", "Ahmed", new Email("ahmed.benali@demo.ma"), null, Role.EMPLOYE),
                LocalDateTime.now().plusHours(1)
        );
        setupToken.markAsUsed();
        when(passwordSetupTokenRepository.findByToken("setup-token")).thenReturn(Optional.of(setupToken));

        assertThatThrownBy(() -> passwordSetupService.setupPassword(
                new SetupPasswordRequest("setup-token", "Password123")
        )).isInstanceOf(PasswordSetupTokenDejaUtiliseException.class);

        verifyNoInteractions(passwordEncoder);
    }
}
