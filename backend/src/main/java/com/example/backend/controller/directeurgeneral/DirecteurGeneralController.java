package com.example.backend.controller.directeurgeneral;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/directeur-general")
public class DirecteurGeneralController {

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> dashboard() {
        return ResponseEntity.ok(Map.of("message", "directeur-general dashboard accessed"));
    }

    @GetMapping("/employe")
    public ResponseEntity<Map<String, String>> employe() {
        return ResponseEntity.ok(Map.of("message", "directeur-general employe accessed"));
    }

    @GetMapping("/demande-employe")
    public ResponseEntity<Map<String, String>> demandeEmploye() {
        return ResponseEntity.ok(Map.of("message", "directeur-general demande-employe accessed"));
    }

    @GetMapping("/historique")
    public ResponseEntity<Map<String, String>> historique() {
        return ResponseEntity.ok(Map.of("message", "directeur-general historique accessed"));
    }

    @GetMapping("/profil")
    public ResponseEntity<Map<String, String>> profil() {
        return ResponseEntity.ok(Map.of("message", "directeur-general profil accessed"));
    }
}
