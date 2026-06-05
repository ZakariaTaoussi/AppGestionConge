import { CommonModule, isPlatformBrowser } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, PLATFORM_ID, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { Role } from '../../../core/enums/role.enum';
import { AdminDepartement } from '../models/admin-departement.model';
import { AdminCreateEmployeRequest, AdminEmploye } from '../models/admin-employe.model';
import { AdminService } from '../services/admin.service';

@Component({
  selector: 'app-admin-employes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './employes.component.html',
  styleUrls: ['./employes.component.scss'],
})
export class AdminEmployesComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly platformId = inject(PLATFORM_ID);

  readonly roles = [
    { value: Role.ADMINISTRATEUR, label: 'Admin' },
    { value: Role.DIRECTEUR_GENERAL, label: 'Directeur General' },
    { value: Role.EMPLOYE, label: 'Employe' },
    { value: Role.RH, label: 'RH' },
    { value: Role.RESPONSABLE, label: 'Responsable' },
  ];
  readonly employes = signal<AdminEmploye[]>([]);
  readonly departements = signal<AdminDepartement[]>([]);
  readonly searchTerm = signal('');
  readonly isLoading = signal(false);
  readonly isSaving = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');
  readonly editingId = signal<number | null>(null);
  readonly deletingId = signal<number | null>(null);
  readonly currentPage = signal(0);
  readonly pageSize = 3;
  readonly totalElements = signal(0);
  readonly totalPages = signal(0);
  form = this.emptyForm();

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.chargerDepartements();
      this.chargerEmployes();
    }
  }

  chargerEmployes(): void {
    this.errorMessage.set('');
    this.isLoading.set(true);
    this.adminService
      .getEmployes(this.currentPage(), this.pageSize, this.searchTerm().trim())
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: response => {
          this.employes.set(response.content);
          this.currentPage.set(response.page);
          this.totalElements.set(response.totalElements);
          this.totalPages.set(response.totalPages);
        },
        error: error => this.errorMessage.set(this.getErrorMessage(error, 'Impossible de charger les employes.')),
      });
  }

  chargerDepartements(): void {
    this.adminService.getDepartements(0, 100).subscribe({
      next: response => {
        this.departements.set(response.content);
        if (!this.form.departementId && response.content.length > 0) {
          this.form.departementId = response.content[0].id;
        }
      },
      error: error => this.errorMessage.set(this.getErrorMessage(error, 'Impossible de charger les departements.')),
    });
  }

  enregistrer(): void {
    const request = this.buildRequest();
    if (!request) {
      return;
    }

    this.errorMessage.set('');
    this.successMessage.set('');
    this.isSaving.set(true);
    const editingId = this.editingId();
    const action = editingId
      ? this.adminService.updateEmploye(editingId, request)
      : this.adminService.createEmploye(request);

    action
      .pipe(finalize(() => this.isSaving.set(false)))
      .subscribe({
        next: employe => {
          const actionMessage = editingId ? 'modifie' : 'cree. Un email a ete envoye';
          this.successMessage.set(`Le compte de ${employe.prenom} ${employe.nom} a ete ${actionMessage}.`);
          this.currentPage.set(0);
          this.reinitialiser();
          this.chargerEmployes();
        },
        error: error => this.errorMessage.set(this.getErrorMessage(error, "Impossible d'enregistrer ce compte.")),
      });
  }

  reinitialiser(): void {
    this.editingId.set(null);
    this.form = this.emptyForm();
  }

  modifier(employe: AdminEmploye): void {
    this.errorMessage.set('');
    this.successMessage.set('');
    this.editingId.set(employe.id);
    this.form = {
      prenom: employe.prenom,
      nom: employe.nom,
      email: employe.email,
      role: employe.role,
      departementId: employe.departementId ?? 0,
    };
  }

  supprimer(employe: AdminEmploye): void {
    const fullName = `${employe.prenom} ${employe.nom}`.trim();
    if (!confirm(`Supprimer le compte de ${fullName || employe.email} ?`)) {
      return;
    }

    this.errorMessage.set('');
    this.successMessage.set('');
    this.deletingId.set(employe.id);

    this.adminService
      .deleteEmploye(employe.id)
      .pipe(finalize(() => this.deletingId.set(null)))
      .subscribe({
        next: () => {
          this.successMessage.set(`Le compte de ${fullName || employe.email} a ete supprime.`);

          if (this.editingId() === employe.id) {
            this.reinitialiser();
          }
          if (this.employes().length === 1 && this.currentPage() > 0) {
            this.currentPage.update(page => page - 1);
          }

          this.chargerEmployes();
        },
        error: error => this.errorMessage.set(this.getErrorMessage(error, 'Impossible de supprimer ce compte.')),
      });
  }

  onSearchChange(value: string): void {
    this.searchTerm.set(value);
    this.currentPage.set(0);
    this.chargerEmployes();
  }

  pagePrecedente(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update(page => page - 1);
      this.chargerEmployes();
    }
  }

  pageSuivante(): void {
    if (this.currentPage() + 1 < this.totalPages()) {
      this.currentPage.update(page => page + 1);
      this.chargerEmployes();
    }
  }

  roleLabel(role: Role): string {
    return this.roles.find(option => option.value === role)?.label ?? role;
  }

  isEditing(employe: AdminEmploye): boolean {
    return this.editingId() === employe.id;
  }

  private buildRequest(): AdminCreateEmployeRequest | null {
    const prenom = this.form.prenom.trim();
    const nom = this.form.nom.trim();
    const email = this.form.email.trim();

    if (!prenom || !nom || !email) {
      this.errorMessage.set('Le prenom, le nom et l email sont obligatoires.');
      return null;
    }
    if (!this.form.departementId) {
      this.errorMessage.set('Le departement est obligatoire.');
      return null;
    }

    return {
      prenom,
      nom,
      email,
      role: this.form.role,
      departementId: this.form.departementId,
    };
  }

  private emptyForm(): AdminCreateEmployeRequest {
    return {
      prenom: '',
      nom: '',
      email: '',
      role: Role.EMPLOYE,
      departementId: this.departements()[0]?.id ?? 0,
    };
  }

  private getErrorMessage(error: unknown, fallback: string): string {
    return error instanceof HttpErrorResponse && typeof error.error?.message === 'string'
      ? error.error.message
      : fallback;
  }
}
