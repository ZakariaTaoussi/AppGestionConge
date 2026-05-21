package com.example.backend.controller.rh;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/rh")
public class RhController {

    @GetMapping("/profil")
    public ResponseEntity<Map<String, String>> profil() {
        return ResponseEntity.ok(Map.of("message", "rh profil accessed"));
    }

    @GetMapping("/notification")
    public ResponseEntity<Map<String, String>> notification() {
        return ResponseEntity.ok(Map.of("message", "rh notification accessed"));
    }

    @GetMapping("/nouvelle-demande")
    public ResponseEntity<Map<String, String>> nouvelleDemande() {
        return ResponseEntity.ok(Map.of("message", "rh nouvelle-demande accessed"));
    }

    @GetMapping("/mes-absences")
    public ResponseEntity<Map<String, String>> mesAbsences() {
        return ResponseEntity.ok(Map.of("message", "rh mes-absences accessed"));
    }

    @GetMapping("/mes-demandes")
    public ResponseEntity<Map<String, String>> mesDemandes() {
        return ResponseEntity.ok(Map.of("message", "rh mes-demandes accessed"));
    }

    @GetMapping("/employes")
    public ResponseEntity<Map<String, String>> employes() {
        return ResponseEntity.ok(Map.of("message", "rh employes accessed"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> dashboard() {
        return ResponseEntity.ok(Map.of("message", "rh dashboard accessed"));
    }
}
