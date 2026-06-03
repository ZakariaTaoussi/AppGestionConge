package com.example.backend.service.mail;

import com.example.backend.domain.Utilisateur;

public interface MailService {

    void sendPasswordSetupEmail(Utilisateur utilisateur, String setupToken);
}
