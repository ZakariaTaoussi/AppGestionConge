package com.example.backend.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> dashboard() {
        return ResponseEntity.ok(Map.of("message", "admin dashboard accessed"));
    }

    @GetMapping("/profil")
    public ResponseEntity<Map<String, String>> profil() {
        return ResponseEntity.ok(Map.of("message", "admin profil accessed"));
    }

    @GetMapping("/regle")
    public ResponseEntity<Map<String, String>> regle() {
        return ResponseEntity.ok(Map.of("message", "admin regle accessed"));
    }

    @GetMapping("/jour-ferie")
    public ResponseEntity<Map<String, String>> jourFerie() {
        return ResponseEntity.ok(Map.of("message", "admin jour-ferie accessed"));
    }

    @GetMapping("/departements")
    public ResponseEntity<Map<String, String>> departements() {
        return ResponseEntity.ok(Map.of("message", "admin departements accessed"));
    }

    @GetMapping("/employes")
    public ResponseEntity<Map<String, String>> employes() {
        return ResponseEntity.ok(Map.of("message", "admin employes accessed"));
    }
}
