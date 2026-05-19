package com.example.backend.controller.utilisateur;

import com.example.backend.dto.utilisateur.CreateUtilisateurRequest;
import com.example.backend.dto.utilisateur.UtilisateurResponse;
import com.example.backend.service.utilisateur.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping
    public ResponseEntity<UtilisateurResponse> creer(@RequestBody CreateUtilisateurRequest request) {
        UtilisateurResponse utilisateur = utilisateurService.creer(request);
        URI location = URI.create("/api/utilisateurs/" + utilisateur.id());

        return ResponseEntity.created(location).body(utilisateur);
    }

    @GetMapping
    public ResponseEntity<List<UtilisateurResponse>> lister() {
        return ResponseEntity.ok(utilisateurService.lister());
    }
}
