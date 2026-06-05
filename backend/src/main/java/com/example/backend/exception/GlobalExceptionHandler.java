package com.example.backend.exception;

import com.example.backend.exception.auth.IdentifiantsInvalidesException;
import com.example.backend.exception.auth.PasswordSetupTokenDejaUtiliseException;
import com.example.backend.exception.auth.PasswordSetupTokenExpireException;
import com.example.backend.exception.auth.PasswordSetupTokenInvalideException;
import com.example.backend.exception.departement.DepartementDejaExisteException;
import com.example.backend.exception.departement.DepartementNonTrouveException;
import com.example.backend.exception.departement.DepartementUtiliseException;
import com.example.backend.exception.departement.ResponsableDejaAffecteException;
import com.example.backend.exception.departement.ResponsableDepartementInvalideException;
import com.example.backend.exception.jourferie.JourFerieNonTrouveException;
import com.example.backend.exception.mail.EmailEnvoiException;
import com.example.backend.exception.utilisateur.UtilisateurDejaExisteException;
import com.example.backend.exception.utilisateur.UtilisateurNonTrouveException;
import com.example.backend.exception.utilisateur.UtilisateurUtiliseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(JourFerieNonTrouveException.class)
    public ResponseEntity<ErrorResponse> handleJourFerieNonTrouveException(
            JourFerieNonTrouveException exception
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DepartementNonTrouveException.class)
    public ResponseEntity<ErrorResponse> handleDepartementNonTrouveException(
            DepartementNonTrouveException exception
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DepartementDejaExisteException.class)
    public ResponseEntity<ErrorResponse> handleDepartementDejaExisteException(
            DepartementDejaExisteException exception
    ) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({
            ResponsableDepartementInvalideException.class,
            ResponsableDejaAffecteException.class,
            DepartementUtiliseException.class
    })
    public ResponseEntity<ErrorResponse> handleConflitDepartement(RuntimeException exception) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(UtilisateurNonTrouveException.class)
    public ResponseEntity<ErrorResponse> handleUtilisateurNonTrouveException(
            UtilisateurNonTrouveException exception
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(UtilisateurDejaExisteException.class)
    public ResponseEntity<ErrorResponse> handleUtilisateurDejaExisteException(
            UtilisateurDejaExisteException exception
    ) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(UtilisateurUtiliseException.class)
    public ResponseEntity<ErrorResponse> handleUtilisateurUtiliseException(
            UtilisateurUtiliseException exception
    ) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(IdentifiantsInvalidesException.class)
    public ResponseEntity<ErrorResponse> handleIdentifiantsInvalidesException(
            IdentifiantsInvalidesException exception
    ) {
        return buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler({
            PasswordSetupTokenInvalideException.class,
            PasswordSetupTokenExpireException.class,
            PasswordSetupTokenDejaUtiliseException.class
    })
    public ResponseEntity<ErrorResponse> handlePasswordSetupTokenException(RuntimeException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmailEnvoiException.class)
    public ResponseEntity<ErrorResponse> handleEmailEnvoiException(EmailEnvoiException exception) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException() {
        return buildResponse(HttpStatus.BAD_REQUEST, "La requete JSON est invalide");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );

        return ResponseEntity.status(status).body(response);
    }
}
