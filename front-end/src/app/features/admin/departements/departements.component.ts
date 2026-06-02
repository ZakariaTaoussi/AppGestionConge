import { CommonModule, isPlatformBrowser } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { AdminDepartement, AdminDepartementRequest, AdminResponsable } from '../models/admin-departement.model';
import { AdminService } from '../services/admin.service';

@Component({
  selector: 'app-admin-departements',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './departements.component.html',
  styleUrls: ['./departements.component.scss'],
})
export class AdminDepartementsComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly platformId = inject(PLATFORM_ID);

  editingId?: number;
  form = this.emptyForm();
  readonly departements = signal<AdminDepartement[]>([]);
  readonly candidats = signal<AdminResponsable[]>([]);
  readonly responsableSearch = signal('');
  readonly selectedResponsable = signal<AdminResponsable | null>(null);
  readonly showSuggestions = signal(false);
  readonly isLoading = signal(false);
  readonly isSaving = signal(false);
  readonly errorMessage = signal('');
  readonly currentPage = signal(0);
  readonly pageSize = 3;
  readonly totalElements = signal(0);
  readonly totalPages = signal(0);
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
    this.adminService
      .getDepartements(this.currentPage(), this.pageSize)
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: response => {
          this.departements.set(response.content);
          this.currentPage.set(response.page);
          this.totalElements.set(response.totalElements);
          this.totalPages.set(response.totalPages);
        },
        error: () => this.errorMessage.set('Impossible de charger les departements.'),
      });
  }

  chargerCandidats(): void {
    this.adminService.getCandidatsResponsables().subscribe({
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

    const request: AdminDepartementRequest = {
      nom,
      responsableId: this.selectedResponsable()?.id ?? null,
    };
    const operation = this.editingId
      ? this.adminService.updateDepartement(this.editingId, request)
      : this.adminService.createDepartement(request);

    this.errorMessage.set('');
    this.isSaving.set(true);
    operation.pipe(finalize(() => this.isSaving.set(false))).subscribe({
      next: () => {
        this.currentPage.set(0);
        this.reinitialiser();
        this.chargerDepartements();
        this.chargerCandidats();
      },
      error: error => this.errorMessage.set(this.getErrorMessage(error, "Impossible d'enregistrer ce departement.")),
    });
  }

  modifier(departement: AdminDepartement): void {
    this.editingId = departement.id;
    this.form = { nom: departement.nom };
    this.selectedResponsable.set(departement.responsable);
    this.responsableSearch.set(departement.responsable ? this.formatResponsable(departement.responsable) : '');
    this.showSuggestions.set(false);
  }

  supprimer(departement: AdminDepartement): void {
    this.errorMessage.set('');
    this.adminService.deleteDepartement(departement.id).subscribe({
      next: () => {
        if (this.editingId === departement.id) this.reinitialiser();
        this.departements.update(departements => departements.filter(item => item.id !== departement.id));
        this.totalElements.update(total => Math.max(total - 1, 0));
        this.totalPages.set(Math.ceil(this.totalElements() / this.pageSize));
        if (this.currentPage() >= this.totalPages() && this.currentPage() > 0) {
          this.currentPage.update(page => page - 1);
        }
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

  selectionnerResponsable(candidat: AdminResponsable): void {
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

  pagePrecedente(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update(page => page - 1);
      this.chargerDepartements();
    }
  }

  pageSuivante(): void {
    if (this.currentPage() + 1 < this.totalPages()) {
      this.currentPage.update(page => page + 1);
      this.chargerDepartements();
    }
  }

  formatResponsable(responsable: AdminResponsable | null): string {
    return responsable ? `${responsable.prenom} ${responsable.nom} - ${responsable.role}` : '';
  }

  private emptyForm(): Pick<AdminDepartementRequest, 'nom'> {
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
