import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, map, of, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Role } from '../enums/role.enum';
import { IAuthService } from '../interfaces/iauth.service';
import { BackendAuthResponse, LoginRequest, LoginResponse, SetupPasswordRequest } from '../models/auth.model';
import { Utilisateur } from '../models/utilisateur.model';

type UserView = {
  nom: string;
  role: Role;
  avatar: string;
  departement: string;
};

@Injectable({ providedIn: 'root' })
export class AuthService implements IAuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  readonly currentUser = signal<Utilisateur | null>(null);

  login(request: LoginRequest): Observable<LoginResponse> {
    const { email, password } = request;

    return this.http
      .post<BackendAuthResponse>(`${this.apiUrl}/login`, { email, password }, { withCredentials: true })
      .pipe(
        map(response => ({
          utilisateur: {
            id: response.id,
            nom: response.nom,
            prenom: response.prenom,
            email: response.email,
            role: response.role,
          },
          message: response.message,
        })),
        tap(response => this.currentUser.set(response.utilisateur)),
      );
  }

  logout(): Observable<void> {
    return this.http
      .post<void>(`${this.apiUrl}/logout`, {}, { withCredentials: true })
      .pipe(tap(() => this.currentUser.set(null)));
  }

  me(): Observable<Utilisateur> {
    return this.http
      .get<Utilisateur>(`${this.apiUrl}/me`, { withCredentials: true })
      .pipe(tap(utilisateur => this.currentUser.set(utilisateur)));
  }

  setupPassword(request: SetupPasswordRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/setup-password`, request, { withCredentials: true });
  }

  ensureCurrentUser(): Observable<Utilisateur | null> {
    const utilisateur = this.currentUser();
    if (utilisateur) {
      return of(utilisateur);
    }

    return this.me().pipe(
      catchError(() => {
        this.currentUser.set(null);
        return of(null);
      }),
    );
  }

  userView(fallback: UserView): UserView {
    const utilisateur = this.currentUser();
    if (!utilisateur) {
      return fallback;
    }

    const fullName = `${utilisateur.prenom} ${utilisateur.nom}`.trim();
    const avatar = `${utilisateur.prenom.charAt(0)}${utilisateur.nom.charAt(0)}`.trim().toUpperCase();

    return {
      ...fallback,
      nom: fullName || fallback.nom,
      role: utilisateur.role,
      avatar: avatar || fallback.avatar,
    };
  }

  logoutAndRedirect(): void {
    this.logout().subscribe({
      next: () => this.router.navigateByUrl('/auth/login'),
      error: () => {
        this.currentUser.set(null);
        this.router.navigateByUrl('/auth/login');
      },
    });
  }

  homeRouteForRole(role: Role): string {
    switch (role) {
      case Role.ADMINISTRATEUR:
        return '/admin/dashboard';
      case Role.RH:
        return '/rh/dashboard';
      case Role.RESPONSABLE:
        return '/responsable/dashboard';
      case Role.DIRECTEUR_GENERAL:
        return '/directeur-general/dashboard';
      case Role.EMPLOYE:
      default:
        return '/employe/dashboard';
    }
  }
}
