package com.example.backend.exception.departement;

public class ResponsableDejaAffecteException extends RuntimeException {

    public ResponsableDejaAffecteException() {
        super("Responsable deja affecte a un departement");
    }
}
