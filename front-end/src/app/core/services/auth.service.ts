import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Injectable, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, finalize, of, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Role } from '../enums/role.enum';
import { LoginRequest, LoginResponse, SetupPasswordRequest } from '../models/auth.model';
import { Utilisateur } from '../models/utilisateur.model';

export interface SessionUserView {
  nom: string;
  role: Role;
  avatar: string;
  departement: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly storageKey = 'gestion-conge-user';
  private readonly isBrowser = isPlatformBrowser(this.platformId);
  private readonly user = signal<Utilisateur | null>(this.loadUser());

  readonly currentUser = this.user.asReadonly();
  readonly role = computed(() => this.user()?.role ?? null);
  readonly isAuthenticated = computed(() => this.user() !== null);

  login(request: LoginRequest): Observable<LoginResponse> {
    const body = {
      email: request.email,
      password: request.password,
    };

    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/auth/login`, body)
      .pipe(tap(response => this.saveUser(response.utilisateur, !!request.remember)));
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/auth/logout`, {}).pipe(
      catchError(() => of(void 0)),
      finalize(() => this.clearUser()),
    );
  }

  setupPassword(request: SetupPasswordRequest): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/auth/setup-password`, request);
  }

  logoutAndRedirect(): void {
    this.logout().subscribe({
      complete: () => this.router.navigateByUrl('/auth/login'),
    });
  }

  userView(fallback: SessionUserView): SessionUserView {
    const user = this.user();

    if (!user) {
      return fallback;
    }

    const fullName = [user.prenom, user.nom].filter(Boolean).join(' ').trim();

    return {
      ...fallback,
      nom: fullName || user.email,
      role: user.role,
      avatar: this.initials(user),
    };
  }

  homeRouteForRole(role: Role | null = this.role()): string {
    switch (role) {
      case Role.ADMINISTRATEUR:
        return '/admin/dashboard';
      case Role.DIRECTEUR_GENERAL:
        return '/directeur-general/dashboard';
      case Role.RH:
        return '/rh/dashboard';
      case Role.RESPONSABLE:
        return '/responsable/dashboard';
      case Role.EMPLOYE:
        return '/employe/dashboard';
      default:
        return '/auth/login';
    }
  }

  hasRole(roles: Role[]): boolean {
    const currentRole = this.role();
    return !!currentRole && roles.includes(currentRole);
  }

  private saveUser(user: Utilisateur, remember: boolean): void {
    const normalizedUser = this.normalizeUser(user);
    this.user.set(normalizedUser);

    if (!this.isBrowser) {
      return;
    }

    const serializedUser = JSON.stringify(normalizedUser);
    localStorage.removeItem(this.storageKey);
    sessionStorage.removeItem(this.storageKey);

    if (remember) {
      localStorage.setItem(this.storageKey, serializedUser);
    } else {
      sessionStorage.setItem(this.storageKey, serializedUser);
    }
  }

  private clearUser(): void {
    this.user.set(null);

    if (!this.isBrowser) {
      return;
    }

    localStorage.removeItem(this.storageKey);
    sessionStorage.removeItem(this.storageKey);
  }

  private loadUser(): Utilisateur | null {
    if (!this.isBrowser) {
      return null;
    }

    return this.readUser(localStorage) ?? this.readUser(sessionStorage);
  }

  private readUser(storage: Storage): Utilisateur | null {
    const rawUser = storage.getItem(this.storageKey);

    if (!rawUser) {
      return null;
    }

    try {
      return this.normalizeUser(JSON.parse(rawUser) as Utilisateur);
    } catch {
      storage.removeItem(this.storageKey);
      return null;
    }
  }

  private normalizeUser(user: Utilisateur): Utilisateur {
    return {
      ...user,
      role: this.normalizeRole(user.role),
    };
  }

  private normalizeRole(role: unknown): Role {
    if (Object.values(Role).includes(role as Role)) {
      return role as Role;
    }

    throw new Error('Role utilisateur invalide');
  }

  private initials(user: Utilisateur): string {
    const initials = [user.prenom, user.nom]
      .filter(Boolean)
      .map(value => value.trim().charAt(0))
      .join('')
      .toUpperCase();

    return initials || user.email.slice(0, 2).toUpperCase();
  }
}
