package com.example.backend.controller.admin;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/profil")
    public ResponseEntity<Map<String, String>> profil() {
        return message("admin profil accessed");
    }

    @GetMapping("/jour-ferie")
    public ResponseEntity<Map<String, String>> jourFerie() {
        return message("admin jour-ferie accessed");
    }

    @GetMapping("/employe")
    public ResponseEntity<Map<String, String>> employe() {
        return message("admin employe accessed");
    }

    @GetMapping("/departement")
    public ResponseEntity<Map<String, String>> departement() {
        return message("admin departement accessed");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> dashboard() {
        return message("admin dashboard accessed");
    }

    private ResponseEntity<Map<String, String>> message(String message) {
        return ResponseEntity.ok(Map.of("message", message));
    }
}
