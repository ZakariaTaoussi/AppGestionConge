package com.example.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "soldes_conge")
public class SoldeConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reliquat")
    private Long id;

    @Column(name = "solde_actuel", nullable = false)
    private Integer soldeActuel;

    @Column(name = "solde_total", nullable = false)
    private Integer soldeTotal;

    @Column(nullable = false)
    private Integer annee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected SoldeConge() {
        // Required by JPA.
    }

    public SoldeConge(Integer soldeActuel, Integer soldeTotal, Integer annee, Utilisateur utilisateur) {
        this.soldeActuel = soldeActuel;
        this.soldeTotal = soldeTotal;
        this.annee = annee;
        this.utilisateur = utilisateur;
    }

    @PrePersist
    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSoldeActuel() {
        return soldeActuel;
    }

    public void setSoldeActuel(Integer soldeActuel) {
        this.soldeActuel = soldeActuel;
    }

    public Integer getSoldeTotal() {
        return soldeTotal;
    }

    public void setSoldeTotal(Integer soldeTotal) {
        this.soldeTotal = soldeTotal;
    }

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
