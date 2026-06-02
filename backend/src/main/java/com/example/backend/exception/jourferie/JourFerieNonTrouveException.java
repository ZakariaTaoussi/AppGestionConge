package com.example.backend.exception.jourferie;

public class JourFerieNonTrouveException extends RuntimeException {

    public JourFerieNonTrouveException(Long id) {
        super("Aucun jour ferie trouve avec l'identifiant : " + id);
    }
}
