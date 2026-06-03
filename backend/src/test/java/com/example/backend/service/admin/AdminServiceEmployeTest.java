package com.example.backend.service.admin;

import com.example.backend.domain.Departement;
import com.example.backend.domain.Email;
import com.example.backend.domain.Role;
import com.example.backend.domain.Utilisateur;
import com.example.backend.dto.admin.AdminCreateEmployeRequest;
import com.example.backend.dto.admin.AdminEmployeResponse;
import com.example.backend.exception.utilisateur.UtilisateurDejaExisteException;
import com.example.backend.repository.DepartementRepository;
import com.example.backend.repository.UtilisateurRepository;
import com.example.backend.service.auth.PasswordSetupService;
import com.example.backend.service.mail.MailService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AdminServiceEmployeTest {

    private final UtilisateurRepository utilisateurRepository = mock(UtilisateurRepository.class);
    private final DepartementRepository departementRepository = mock(DepartementRepository.class);
    private final PasswordSetupService passwordSetupService = mock(PasswordSetupService.class);
    private final MailService mailService = mock(MailService.class);
    private final AdminServiceEmploye adminServiceEmploye = new AdminServiceEmploye(
            utilisateurRepository,
            departementRepository,
            passwordSetupService,
            mailService
    );

    @Test
    void createEmployeCreeUnUtilisateurSansMotDePasseEtAvecTokenSetup() {
        Departement departement = new Departement("Informatique", null);
        departement.setId(1L);
        when(departementRepository.findById(1L)).thenReturn(Optional.of(departement));
        when(utilisateurRepository.findByEmailValue("ahmed.benali@demo.ma")).thenReturn(Optional.empty());
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> {
            Utilisateur utilisateur = invocation.getArgument(0);
            utilisateur.setId(10L);
            return utilisateur;
        });
        when(passwordSetupService.createTokenFor(any(Utilisateur.class))).thenReturn("setup-token-temporaire");

        AdminEmployeResponse response = adminServiceEmploye.createEmploye(new AdminCreateEmployeRequest(
                " Benali ",
                " Ahmed ",
                " AHMED.BENALI@DEMO.MA ",
                Role.EMPLOYE,
                1L
        ));

        ArgumentCaptor<Utilisateur> utilisateurCaptor = ArgumentCaptor.forClass(Utilisateur.class);
        verify(utilisateurRepository).save(utilisateurCaptor.capture());
        Utilisateur savedUtilisateur = utilisateurCaptor.getValue();
        verify(passwordSetupService).createTokenFor(savedUtilisateur);
        verify(mailService).sendPasswordSetupEmail(savedUtilisateur, "setup-token-temporaire");

        assertThat(savedUtilisateur.getPassword()).isNull();
        assertThat(savedUtilisateur.getDepartement()).isEqualTo(departement);
        assertThat(response).isEqualTo(new AdminEmployeResponse(
                10L,
                "Benali",
                "Ahmed",
                "ahmed.benali@demo.ma",
                Role.EMPLOYE,
                "Informatique",
                "setup-token-temporaire"
        ));
    }

    @Test
    void createEmployeRefuseUnEmailDejaUtiliseSansGenererDeToken() {
        Departement departement = new Departement("Informatique", null);
        departement.setId(1L);
        when(departementRepository.findById(1L)).thenReturn(Optional.of(departement));
        when(utilisateurRepository.findByEmailValue("ahmed.benali@demo.ma"))
                .thenReturn(Optional.of(new Utilisateur(
                        "Benali",
                        "Ahmed",
                        new Email("ahmed.benali@demo.ma"),
                        null,
                        Role.EMPLOYE
                )));

        assertThatThrownBy(() -> adminServiceEmploye.createEmploye(new AdminCreateEmployeRequest(
                "Benali",
                "Ahmed",
                "ahmed.benali@demo.ma",
                Role.EMPLOYE,
                1L
        ))).isInstanceOf(UtilisateurDejaExisteException.class);

        verifyNoInteractions(passwordSetupService);
        verifyNoInteractions(mailService);
    }
}
