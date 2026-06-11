package com.example.backend.controller.admin;

import com.example.backend.dto.agenda.AgendaResponse;
import com.example.backend.dto.agenda.CreateAgendaRequest;
import com.example.backend.dto.agenda.JourCalendrierResponse;
import com.example.backend.service.interfaces.IGestionAgendaService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/agendas")
@RequiredArgsConstructor
public class AdminAgendaController {

    private final IGestionAgendaService agendaService;

    @PostMapping({"", "/"})
    public ResponseEntity<AgendaResponse> creerAgenda(@Valid @RequestBody CreateAgendaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendaService.creerAgenda(request));
    }

    @GetMapping({"", "/"})
    public ResponseEntity<List<AgendaResponse>> getAllAgendas() {
        return ResponseEntity.ok(agendaService.getAllAgendas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaResponse> getAgendaById(@PathVariable Long id) {
        return ResponseEntity.ok(agendaService.getAgendaById(id));
    }

    @GetMapping("/{agendaId}/jours-calendrier")
    public ResponseEntity<List<JourCalendrierResponse>> getJoursCalendrier(@PathVariable Long agendaId) {
        return ResponseEntity.ok(agendaService.getJoursCalendrier(agendaId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerAgenda(@PathVariable Long id) {
        agendaService.supprimerAgenda(id);
        return ResponseEntity.noContent().build();
    }
}
