package com.example.backend.controller.employe;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employe")
public class EmployeController {

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> dashboard() {
        return message("employe dashboard accessed");
    }

    @GetMapping("/mes-demandes")
    public ResponseEntity<Map<String, String>> mesDemandes() {
        return message("employe mes-demandes accessed");
    }

    @GetMapping("/mes-absences")
    public ResponseEntity<Map<String, String>> mesAbsences() {
        return message("employe mes-absences accessed");
    }

    @GetMapping("/nouvelle-demande")
    public ResponseEntity<Map<String, String>> nouvelleDemande() {
        return message("employe nouvelle-demande accessed");
    }

    @GetMapping("/notification")
    public ResponseEntity<Map<String, String>> notification() {
        return message("employe notification accessed");
    }

    @GetMapping("/profil")
    public ResponseEntity<Map<String, String>> profil() {
        return message("employe profil accessed");
    }

    private ResponseEntity<Map<String, String>> message(String message) {
        return ResponseEntity.ok(Map.of("message", message));
    }
}
