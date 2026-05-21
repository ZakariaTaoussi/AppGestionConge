package com.example.backend.controller.employe;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/employe")
public class EmployeController {

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> dashboard() {
        return ResponseEntity.ok(Map.of("message", "employe dashboard accessed"));
    }

    @GetMapping("/mes-demandes")
    public ResponseEntity<Map<String, String>> mesDemandes() {
        return ResponseEntity.ok(Map.of("message", "employe mes-demandes accessed"));
    }

    @GetMapping("/mes-absences")
    public ResponseEntity<Map<String, String>> mesAbsences() {
        return ResponseEntity.ok(Map.of("message", "employe mes-absences accessed"));
    }

    @GetMapping("/nouvelle-demande")
    public ResponseEntity<Map<String, String>> nouvelleDemande() {
        return ResponseEntity.ok(Map.of("message", "employe nouvelle-demande accessed"));
    }

    @GetMapping("/notification")
    public ResponseEntity<Map<String, String>> notification() {
        return ResponseEntity.ok(Map.of("message", "employe notification accessed"));
    }

    @GetMapping("/profil")
    public ResponseEntity<Map<String, String>> profil() {
        return ResponseEntity.ok(Map.of("message", "employe profil accessed"));
    }
}
