package com.example.backend.service.interfaces;

import com.example.backend.dto.utilisateur.CreateUtilisateurRequest;
import com.example.backend.dto.utilisateur.UtilisateurResponse;
import java.util.List;

public interface IUtilisateurService {
    UtilisateurResponse creerUtilisateur(CreateUtilisateurRequest request);

    List<UtilisateurResponse> getAllUtilisateurs();

    UtilisateurResponse getUtilisateurById(Long id);
}
