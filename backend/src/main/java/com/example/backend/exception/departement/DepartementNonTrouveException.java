package com.example.backend.exception.departement;

public class DepartementNonTrouveException extends RuntimeException {

    public DepartementNonTrouveException(Long id) {
        super("Aucun departement trouve avec l'identifiant : " + id);
    }
}
