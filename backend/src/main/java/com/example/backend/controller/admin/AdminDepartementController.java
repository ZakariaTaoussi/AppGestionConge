package com.example.backend.controller.admin;

import com.example.backend.dto.departement.AffecterResponsableRequest;
import com.example.backend.dto.departement.CreateDepartementRequest;
import com.example.backend.dto.departement.DepartementResponse;
import com.example.backend.dto.departement.UpdateDepartementRequest;
import com.example.backend.dto.responsable.ResponsableCandidatResponse;
import com.example.backend.service.interfaces.IDepartementService;
import com.example.backend.service.interfaces.IResponsableService;
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
@RequestMapping("/api/admin/departements")
@RequiredArgsConstructor
public class AdminDepartementController {

    private final IDepartementService departementService;
    private final IResponsableService responsableService;

    @PostMapping({"", "/"})
    public ResponseEntity<DepartementResponse> creerDepartement(@Valid @RequestBody CreateDepartementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departementService.creerDepartement(request));
    }

    @GetMapping({"", "/"})
    public ResponseEntity<List<DepartementResponse>> getAllDepartements() {
        return ResponseEntity.ok(departementService.getAllDepartements());
    }

    @GetMapping("/responsables-candidats")
    public ResponseEntity<List<ResponsableCandidatResponse>> getResponsablesCandidats() {
        return ResponseEntity.ok(responsableService.getCandidatsResponsables());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartementResponse> getDepartementById(@PathVariable Long id) {
        return ResponseEntity.ok(departementService.getDepartementById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartementResponse> modifierDepartement(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDepartementRequest request
    ) {
        return ResponseEntity.ok(departementService.modifierDepartement(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerDepartement(@PathVariable Long id) {
        departementService.supprimerDepartement(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/responsable")
    public ResponseEntity<DepartementResponse> affecterResponsable(
            @PathVariable Long id,
            @Valid @RequestBody AffecterResponsableRequest request
    ) {
        return ResponseEntity.ok(departementService.affecterResponsable(id, request));
    }
}
