import { Routes } from '@angular/router';
import { Role } from './core/enums/role.enum';
import { roleGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () =>
      import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES),
  },


  {
    path: 'employe',
    canMatch: [roleGuard],
    data: { roles: [Role.EMPLOYE] },
    loadChildren: () =>
      import('./features/employe/employe.routes').then(m => m.EMPLOYE_ROUTES),
  },
  {
    path: 'responsable',
    canMatch: [roleGuard],
    data: { roles: [Role.RESPONSABLE] },
    loadChildren: () =>
      import('./features/responsable/responsable.routes').then(m => m.RESPONSABLE_ROUTES),
  },
  {
    path: 'rh',
    canMatch: [roleGuard],
    data: { roles: [Role.RH] },
    loadChildren: () =>
      import('./features/rh/rh.routes').then(m => m.RH_ROUTES),
  },
  {
    path: 'directeur-general',
    canMatch: [roleGuard],
    data: { roles: [Role.DIRECTEUR_GENERAL] },
    loadChildren: () =>
      import('./features/directeur-general/dg.route').then(m => m.DIRECTEUR_GENERAL_ROUTES),
  },
  {
    path: 'admin',
    canMatch: [roleGuard],
    data: { roles: [Role.ADMINISTRATEUR] },
    loadChildren: () =>
      import('./features/admin/admin.route').then(m => m.ADMIN_ROUTES),
  },


  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
  { path: '**', redirectTo: 'auth/login' },
];
