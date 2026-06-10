package com.example.backend.controller.rh;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rh")
public class RhController {

    @GetMapping("/profil")
    public ResponseEntity<Map<String, String>> profil() {
        return message("rh profil accessed");
    }

    @GetMapping("/notification")
    public ResponseEntity<Map<String, String>> notification() {
        return message("rh notification accessed");
    }

    @GetMapping("/nouvelle-demande")
    public ResponseEntity<Map<String, String>> nouvelleDemande() {
        return message("rh nouvelle-demande accessed");
    }

    @GetMapping("/mes-absences")
    public ResponseEntity<Map<String, String>> mesAbsences() {
        return message("rh mes-absences accessed");
    }

    @GetMapping("/mes-demandes")
    public ResponseEntity<Map<String, String>> mesDemandes() {
        return message("rh mes-demandes accessed");
    }

    @GetMapping("/employes")
    public ResponseEntity<Map<String, String>> employes() {
        return message("rh employes accessed");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> dashboard() {
        return message("rh dashboard accessed");
    }

    private ResponseEntity<Map<String, String>> message(String message) {
        return ResponseEntity.ok(Map.of("message", message));
    }
}
