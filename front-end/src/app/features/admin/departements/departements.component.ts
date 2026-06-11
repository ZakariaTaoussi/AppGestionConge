import { CommonModule, isPlatformBrowser } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { CreateDepartementRequest, Departement } from '../models/departement.model';
import { ResponsableCandidat } from '../models/responsable-candidat.model';
import { AdminDepartementService } from '../services/admin-departement.service';
import { AdminResponsableService } from '../services/admin-responsable.service';

@Component({
  selector: 'app-admin-departements',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './departements.component.html',
  styleUrls: ['./departements.component.scss'],
})
export class AdminDepartementsComponent implements OnInit {
  private readonly departementService = inject(AdminDepartementService);
  private readonly responsableService = inject(AdminResponsableService);
  private readonly platformId = inject(PLATFORM_ID);

  editingId?: number;
  form = this.emptyForm();
  readonly departements = signal<Departement[]>([]);
  readonly candidats = signal<ResponsableCandidat[]>([]);
  readonly responsableSearch = signal('');
  readonly selectedResponsable = signal<ResponsableCandidat | null>(null);
  readonly showSuggestions = signal(false);
  readonly isLoading = signal(false);
  readonly isSaving = signal(false);
  readonly errorMessage = signal('');
  readonly totalElements = computed(() => this.departements().length);
  readonly candidatsFiltres = computed(() => {
    const recherche = this.normalize(this.responsableSearch());
    if (!recherche) return [];

    return this.candidats().filter(candidat =>
      this.normalize(`${candidat.prenom} ${candidat.nom}`).includes(recherche)
    );
  });

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.chargerDepartements();
      this.chargerCandidats();
    }
  }

  chargerDepartements(): void {
    this.errorMessage.set('');
    this.isLoading.set(true);
    this.departementService
      .getDepartements()
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: departements => this.departements.set(departements),
        error: () => this.errorMessage.set('Impossible de charger les departements.'),
      });
  }

  chargerCandidats(): void {
    this.responsableService.getCandidatsResponsables().subscribe({
      next: candidats => this.candidats.set(candidats),
      error: () => this.errorMessage.set('Impossible de charger les responsables disponibles.'),
    });
  }

  enregistrer(): void {
    const nom = this.form.nom.trim();
    if (!nom) {
      this.errorMessage.set('Le nom du departement est obligatoire.');
      return;
    }
    if (this.responsableSearch().trim() && !this.selectedResponsable()) {
      this.errorMessage.set('Selectionnez un responsable dans la liste proposee.');
      return;
    }

    const request: CreateDepartementRequest = {
      nom,
      responsableId: this.selectedResponsable()?.utilisateurId ?? null,
    };
    const operation = this.editingId
      ? this.departementService.updateDepartement(this.editingId, request)
      : this.departementService.createDepartement(request);

    this.errorMessage.set('');
    this.isSaving.set(true);
    operation.pipe(finalize(() => this.isSaving.set(false))).subscribe({
      next: () => {
        this.reinitialiser();
        this.chargerDepartements();
        this.chargerCandidats();
      },
      error: error => this.errorMessage.set(this.getErrorMessage(error, "Impossible d'enregistrer ce departement.")),
    });
  }

  modifier(departement: Departement): void {
    this.editingId = departement.id;
    this.form = { nom: departement.nom };
    const responsable = this.findResponsable(departement.responsableId);
    this.selectedResponsable.set(responsable);
    this.responsableSearch.set(responsable ? this.formatResponsable(responsable) : this.formatResponsableFromDepartement(departement));
    this.showSuggestions.set(false);
  }

  supprimer(departement: Departement): void {
    this.errorMessage.set('');
    this.departementService.deleteDepartement(departement.id).subscribe({
      next: () => {
        if (this.editingId === departement.id) this.reinitialiser();
        this.departements.update(departements => departements.filter(item => item.id !== departement.id));
        this.chargerDepartements();
        this.chargerCandidats();
      },
      error: error => this.errorMessage.set(this.getErrorMessage(error, 'Impossible de supprimer ce departement.')),
    });
  }

  onResponsableSearchChange(value: string): void {
    this.responsableSearch.set(value);
    if (value !== this.formatResponsable(this.selectedResponsable())) {
      this.selectedResponsable.set(null);
    }
    this.showSuggestions.set(Boolean(value.trim()));
  }

  afficherSuggestions(): void {
    this.showSuggestions.set(Boolean(this.responsableSearch().trim()));
  }

  selectionnerResponsable(candidat: ResponsableCandidat): void {
    this.selectedResponsable.set(candidat);
    this.responsableSearch.set(this.formatResponsable(candidat));
    this.showSuggestions.set(false);
  }

  retirerResponsable(): void {
    this.selectedResponsable.set(null);
    this.responsableSearch.set('');
    this.showSuggestions.set(false);
  }

  masquerSuggestions(): void {
    this.showSuggestions.set(false);
  }

  reinitialiser(): void {
    this.editingId = undefined;
    this.form = this.emptyForm();
    this.retirerResponsable();
  }

  formatResponsable(responsable: ResponsableCandidat | null): string {
    return responsable ? `${responsable.prenom} ${responsable.nom} - ${responsable.role}` : '';
  }

  formatResponsableFromDepartement(departement: Departement): string {
    return departement.responsablePrenom && departement.responsableNom
      ? `${departement.responsablePrenom} ${departement.responsableNom}`
      : '';
  }

  private findResponsable(id: number | null): ResponsableCandidat | null {
    return id ? this.candidats().find(candidat => candidat.id === id || candidat.utilisateurId === id) ?? null : null;
  }

  private emptyForm(): Pick<CreateDepartementRequest, 'nom'> {
    return { nom: '' };
  }

  private normalize(value: string): string {
    return value.trim().toLocaleLowerCase('fr-FR');
  }

  private getErrorMessage(error: unknown, fallback: string): string {
    return error instanceof HttpErrorResponse && typeof error.error?.message === 'string'
      ? error.error.message
      : fallback;
  }
}
