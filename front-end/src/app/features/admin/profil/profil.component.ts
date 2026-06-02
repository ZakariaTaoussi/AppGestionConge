import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, OnInit, PLATFORM_ID, inject, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { Role } from '../../../core/enums/role.enum';
import { AdminProfil } from '../models/admin-profil.model';
import { AdminService } from '../services/admin.service';

@Component({
  selector: 'app-admin-profil',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profil.component.html',
  styleUrls: ['./profil.component.scss'],
})
export class AdminProfilComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly platformId = inject(PLATFORM_ID);

  readonly profil = signal<AdminProfil | null>(null);
  readonly isLoading = signal(true);
  readonly errorMessage = signal('');

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    this.chargerProfil();
  }

  chargerProfil(): void {
    this.errorMessage.set('');
    this.isLoading.set(true);

    this.adminService
      .getProfil()
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: profil => this.profil.set(profil),
        error: () => this.errorMessage.set('Impossible de charger votre profil.'),
      });
  }

  get roleLabel(): string {
    switch (this.profil()?.role) {
      case Role.ADMINISTRATEUR:
        return 'Administrateur';
      default:
        return this.profil()?.role ?? '';
    }
  }

  get avatar(): string {
    const profil = this.profil();
    if (!profil) {
      return '';
    }

    return `${profil.prenom.charAt(0)}${profil.nom.charAt(0)}`.toUpperCase();
  }
}
