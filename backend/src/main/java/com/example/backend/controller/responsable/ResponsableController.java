package com.example.backend.controller.responsable;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/responsable")
public class ResponsableController {

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> dashboard() {
        return message("responsable dashboard accessed");
    }

    @GetMapping("/mes-employes")
    public ResponseEntity<Map<String, String>> mesEmployes() {
        return message("responsable mes-employes accessed");
    }

    @GetMapping("/mes-demandes")
    public ResponseEntity<Map<String, String>> mesDemandes() {
        return message("responsable mes-demandes accessed");
    }

    @GetMapping("/mes-absences")
    public ResponseEntity<Map<String, String>> mesAbsences() {
        return message("responsable mes-absences accessed");
    }

    @GetMapping("/nouvelle-demande")
    public ResponseEntity<Map<String, String>> nouvelleDemande() {
        return message("responsable nouvelle-demande accessed");
    }

    @GetMapping("/historique")
    public ResponseEntity<Map<String, String>> historique() {
        return message("responsable historique accessed");
    }

    @GetMapping("/notifications")
    public ResponseEntity<Map<String, String>> notifications() {
        return message("responsable notifications accessed");
    }

    @GetMapping("/profil")
    public ResponseEntity<Map<String, String>> profil() {
        return message("responsable profil accessed");
    }

    private ResponseEntity<Map<String, String>> message(String message) {
        return ResponseEntity.ok(Map.of("message", message));
    }
}
