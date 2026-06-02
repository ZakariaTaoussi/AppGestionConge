package com.example.backend.controller.admin;

import com.example.backend.dto.admin.AdminDepartementRequest;
import com.example.backend.dto.admin.AdminDepartementResponse;
import com.example.backend.dto.admin.AdminJourFerieRequest;
import com.example.backend.dto.admin.AdminJourFerieResponse;
import com.example.backend.dto.admin.AdminPageResponse;
import com.example.backend.dto.admin.AdminProfilResponse;
import com.example.backend.dto.admin.AdminResponsableResponse;
import com.example.backend.service.admin.AdminService;
import com.example.backend.service.admin.AdminServiceDepartement;
import com.example.backend.service.admin.AdminServiceJourFerie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminServiceJourFerie adminServiceJourFerie;
    private final AdminServiceDepartement adminServiceDepartement;

    public AdminController(
            AdminService adminService,
            AdminServiceJourFerie adminServiceJourFerie,
            AdminServiceDepartement adminServiceDepartement
    ) {
        this.adminService = adminService;
        this.adminServiceJourFerie = adminServiceJourFerie;
        this.adminServiceDepartement = adminServiceDepartement;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> dashboard() {
        return ResponseEntity.ok(Map.of("message", "admin dashboard accessed"));
    }

    @GetMapping("/profil")
    public ResponseEntity<AdminProfilResponse> profil(
            @RequestAttribute("currentUserEmail") String currentUserEmail
    ) {
        return ResponseEntity.ok(adminService.getProfil(currentUserEmail));
    }

    @GetMapping("/regle")
    public ResponseEntity<Map<String, String>> regle() {
        return ResponseEntity.ok(Map.of("message", "admin regle accessed"));
    }

    @GetMapping("/jours-feries")
    public ResponseEntity<AdminPageResponse<AdminJourFerieResponse>> getJoursFeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminServiceJourFerie.getJoursFeries(page, size));
    }

    @PostMapping("/jours-feries")
    public ResponseEntity<AdminJourFerieResponse> createJourFerie(@RequestBody AdminJourFerieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminServiceJourFerie.createJourFerie(request));
    }

    @PutMapping("/jours-feries/{id}")
    public ResponseEntity<AdminJourFerieResponse> updateJourFerie(
            @PathVariable Long id,
            @RequestBody AdminJourFerieRequest request
    ) {
        return ResponseEntity.ok(adminServiceJourFerie.updateJourFerie(id, request));
    }

    @DeleteMapping("/jours-feries/{id}")
    public ResponseEntity<Void> deleteJourFerie(@PathVariable Long id) {
        adminServiceJourFerie.deleteJourFerie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/departements")
    public ResponseEntity<AdminPageResponse<AdminDepartementResponse>> getDepartements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        return ResponseEntity.ok(adminServiceDepartement.getDepartements(page, size));
    }

    @GetMapping("/departements/responsables-candidats")
    public ResponseEntity<List<AdminResponsableResponse>> getCandidatsResponsables() {
        return ResponseEntity.ok(adminServiceDepartement.getCandidatsResponsables());
    }

    @PostMapping("/departements")
    public ResponseEntity<AdminDepartementResponse> createDepartement(@RequestBody AdminDepartementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminServiceDepartement.createDepartement(request));
    }

    @PutMapping("/departements/{id}")
    public ResponseEntity<AdminDepartementResponse> updateDepartement(
            @PathVariable Long id,
            @RequestBody AdminDepartementRequest request
    ) {
        return ResponseEntity.ok(adminServiceDepartement.updateDepartement(id, request));
    }

    @DeleteMapping("/departements/{id}")
    public ResponseEntity<Void> deleteDepartement(@PathVariable Long id) {
        adminServiceDepartement.deleteDepartement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employes")
    public ResponseEntity<Map<String, String>> employes() {
        return ResponseEntity.ok(Map.of("message", "admin employes accessed"));
    }
}
