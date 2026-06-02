package com.example.backend.exception.departement;

public class DepartementDejaExisteException extends RuntimeException {

    public DepartementDejaExisteException(String nom) {
        super("Un departement avec le nom " + nom + " existe deja");
    }
}
