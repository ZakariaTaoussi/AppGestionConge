package com.example.backend.controller.responsable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/responsable")
public class ResponsableController {

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> dashboard() {
        return ResponseEntity.ok(Map.of("message", "responsable dashboard accessed"));
    }

    @GetMapping("/mes-employes")
    public ResponseEntity<Map<String, String>> mesEmployes() {
        return ResponseEntity.ok(Map.of("message", "responsable mes-employes accessed"));
    }

    @GetMapping("/mes-demandes")
    public ResponseEntity<Map<String, String>> mesDemandes() {
        return ResponseEntity.ok(Map.of("message", "responsable mes-demandes accessed"));
    }

    @GetMapping("/mes-absences")
    public ResponseEntity<Map<String, String>> mesAbsences() {
        return ResponseEntity.ok(Map.of("message", "responsable mes-absences accessed"));
    }

    @GetMapping("/nouvelle-demande")
    public ResponseEntity<Map<String, String>> nouvelleDemande() {
        return ResponseEntity.ok(Map.of("message", "responsable nouvelle-demande accessed"));
    }

    @GetMapping("/historique")
    public ResponseEntity<Map<String, String>> historique() {
        return ResponseEntity.ok(Map.of("message", "responsable historique accessed"));
    }

    @GetMapping("/notifications")
    public ResponseEntity<Map<String, String>> notifications() {
        return ResponseEntity.ok(Map.of("message", "responsable notifications accessed"));
    }

    @GetMapping("/profil")
    public ResponseEntity<Map<String, String>> profil() {
        return ResponseEntity.ok(Map.of("message", "responsable profil accessed"));
    }
}
