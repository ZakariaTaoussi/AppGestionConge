package com.example.backend.service.mail;

import com.example.backend.domain.Utilisateur;
import com.example.backend.exception.mail.EmailEnvoiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpMailService implements MailService {

    private final JavaMailSender javaMailSender;
    private final boolean mailEnabled;
    private final String from;
    private final String setupPasswordUrl;

    public SmtpMailService(
            ObjectProvider<JavaMailSender> javaMailSenderProvider,
            @Value("${app.mail.enabled:false}") boolean mailEnabled,
            @Value("${app.mail.from:no-reply@gestionconge.local}") String from,
            @Value("${app.frontend.setup-password-url:http://localhost:4200/setup-password}") String setupPasswordUrl
    ) {
        this.javaMailSender = javaMailSenderProvider.getIfAvailable();
        this.mailEnabled = mailEnabled;
        this.from = from;
        this.setupPasswordUrl = setupPasswordUrl;
    }

    @Override
    public void sendPasswordSetupEmail(Utilisateur utilisateur, String setupToken) {
        if (!mailEnabled) {
            return;
        }

        String email = utilisateur.getEmail().getValue();
        if (javaMailSender == null) {
            throw new EmailEnvoiException(email);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Creation de votre compte GestionConge");
        message.setText(buildMessage(utilisateur, setupToken));

        try {
            javaMailSender.send(message);
        } catch (MailException exception) {
            throw new EmailEnvoiException(email);
        }
    }

    private String buildMessage(Utilisateur utilisateur, String setupToken) {
        String setupLink = setupPasswordUrl + "?token=" + setupToken;

        return """
                Bonjour %s %s,

                Votre compte GestionConge a ete cree.

                Pour definir votre mot de passe, cliquez sur le lien suivant :
                %s

                Ce lien expire dans 24 heures.
                """.formatted(utilisateur.getPrenom(), utilisateur.getNom(), setupLink);
    }
}
