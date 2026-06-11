package com.example.backend.service.interfaces;

import com.example.backend.dto.responsable.ResponsableCandidatResponse;
import com.example.backend.model.Responsable;
import java.util.List;

public interface IResponsableService {
    List<ResponsableCandidatResponse> getCandidatsResponsables();

    Responsable convertirEnResponsable(Long employeId);
}
