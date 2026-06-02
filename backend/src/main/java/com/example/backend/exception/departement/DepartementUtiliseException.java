package com.example.backend.exception.departement;

public class DepartementUtiliseException extends RuntimeException {

    public DepartementUtiliseException(Long id) {
        super("Le departement " + id + " ne peut pas etre supprime car des utilisateurs y sont affectes");
    }
}
