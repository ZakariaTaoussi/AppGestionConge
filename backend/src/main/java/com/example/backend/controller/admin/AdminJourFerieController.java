package com.example.backend.controller.admin;

import com.example.backend.dto.jourferie.CreateJourFerieRequest;
import com.example.backend.dto.jourferie.CreateMultipleJoursFeriesRequest;
import com.example.backend.dto.jourferie.JourFerieResponse;
import com.example.backend.service.interfaces.IGestionJourFerieService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/jours-feries")
@RequiredArgsConstructor
public class AdminJourFerieController {

    private final IGestionJourFerieService jourFerieService;

    @PostMapping({"", "/"})
    public ResponseEntity<JourFerieResponse> creerJourFerie(@Valid @RequestBody CreateJourFerieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jourFerieService.creerJourFerie(request));
    }

    @PostMapping("/multiple")
    public ResponseEntity<List<JourFerieResponse>> creerPlusieursJoursFeries(
            @Valid @RequestBody CreateMultipleJoursFeriesRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jourFerieService.creerPlusieursJoursFeries(request));
    }

    @GetMapping({"", "/"})
    public ResponseEntity<List<JourFerieResponse>> getAllJoursFeries() {
        return ResponseEntity.ok(jourFerieService.getAllJoursFeries());
    }

    @GetMapping("/agenda/{agendaId}")
    public ResponseEntity<List<JourFerieResponse>> getJoursFeriesByAgenda(@PathVariable Long agendaId) {
        return ResponseEntity.ok(jourFerieService.getJoursFeriesByAgenda(agendaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JourFerieResponse> modifierJourFerie(
            @PathVariable Long id,
            @Valid @RequestBody CreateJourFerieRequest request
    ) {
        return ResponseEntity.ok(jourFerieService.modifierJourFerie(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerJourFerie(@PathVariable Long id) {
        jourFerieService.supprimerJourFerie(id);
        return ResponseEntity.noContent().build();
    }
}
