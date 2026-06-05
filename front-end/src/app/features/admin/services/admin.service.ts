import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AdminDepartement, AdminDepartementRequest, AdminResponsable } from '../models/admin-departement.model';
import { AdminCreateEmployeRequest, AdminEmploye, AdminUpdateEmployeRequest } from '../models/admin-employe.model';
import { AdminJourFerie, AdminJourFerieRequest, AdminPage } from '../models/admin-jour-ferie.model';
import { AdminProfil } from '../models/admin-profil.model';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly http = inject(HttpClient);

  getProfil(): Observable<AdminProfil> {
    return this.http.get<AdminProfil>(`${environment.apiUrl}/admin/profil`);
  }

  getJoursFeries(page: number, size: number): Observable<AdminPage<AdminJourFerie>> {
    return this.http.get<AdminPage<AdminJourFerie>>(`${environment.apiUrl}/admin/jours-feries`, {
      params: { page, size },
    });
  }

  createJourFerie(request: AdminJourFerieRequest): Observable<AdminJourFerie> {
    return this.http.post<AdminJourFerie>(`${environment.apiUrl}/admin/jours-feries`, request);
  }

  updateJourFerie(id: number, request: AdminJourFerieRequest): Observable<AdminJourFerie> {
    return this.http.put<AdminJourFerie>(`${environment.apiUrl}/admin/jours-feries/${id}`, request);
  }

  deleteJourFerie(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/admin/jours-feries/${id}`);
  }

  getDepartements(page: number, size: number): Observable<AdminPage<AdminDepartement>> {
    return this.http.get<AdminPage<AdminDepartement>>(`${environment.apiUrl}/admin/departements`, {
      params: { page, size },
    });
  }

  getCandidatsResponsables(): Observable<AdminResponsable[]> {
    return this.http.get<AdminResponsable[]>(`${environment.apiUrl}/admin/departements/responsables-candidats`);
  }

  createDepartement(request: AdminDepartementRequest): Observable<AdminDepartement> {
    return this.http.post<AdminDepartement>(`${environment.apiUrl}/admin/departements`, request);
  }

  updateDepartement(id: number, request: AdminDepartementRequest): Observable<AdminDepartement> {
    return this.http.put<AdminDepartement>(`${environment.apiUrl}/admin/departements/${id}`, request);
  }

  deleteDepartement(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/admin/departements/${id}`);
  }

  getEmployes(page: number, size: number, search = ''): Observable<AdminPage<AdminEmploye>> {
    return this.http.get<AdminPage<AdminEmploye>>(`${environment.apiUrl}/admin/employes`, {
      params: { page, size, search },
    });
  }

  createEmploye(request: AdminCreateEmployeRequest): Observable<AdminEmploye> {
    return this.http.post<AdminEmploye>(`${environment.apiUrl}/admin/employes`, request);
  }

  updateEmploye(id: number, request: AdminUpdateEmployeRequest): Observable<AdminEmploye> {
    return this.http.put<AdminEmploye>(`${environment.apiUrl}/admin/employes/${id}`, request);
  }

  deleteEmploye(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/admin/employes/${id}`);
  }
}
