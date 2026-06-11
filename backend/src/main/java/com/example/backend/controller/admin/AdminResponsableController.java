package com.example.backend.controller.admin;

import com.example.backend.dto.responsable.ResponsableCandidatResponse;
import com.example.backend.service.interfaces.IResponsableService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/responsables")
@RequiredArgsConstructor
public class AdminResponsableController {

    private final IResponsableService responsableService;

    @GetMapping("/candidats")
    public ResponseEntity<List<ResponsableCandidatResponse>> getCandidatsResponsables() {
        return ResponseEntity.ok(responsableService.getCandidatsResponsables());
    }
}
