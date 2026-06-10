import { CanActivateFn, CanMatchFn, Route, Router, UrlSegment } from '@angular/router';
import { inject } from '@angular/core';
import { map } from 'rxjs';
import { Role } from '../enums/role.enum';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (_route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.ensureCurrentUser().pipe(
    map(utilisateur =>
      utilisateur ? true : router.createUrlTree(['/auth/login'], { queryParams: { returnUrl: state.url } }),
    ),
  );
};

export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.ensureCurrentUser().pipe(
    map(utilisateur => (utilisateur ? router.createUrlTree([authService.homeRouteForRole(utilisateur.role)]) : true)),
  );
};

export const roleGuard: CanMatchFn = (route: Route, segments: UrlSegment[]) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const allowedRoles = (route.data?.['roles'] as Role[] | undefined) ?? [];
  const returnUrl = `/${segments.map(segment => segment.path).join('/')}`;

  return authService.ensureCurrentUser().pipe(
    map(utilisateur => {
      if (!utilisateur) {
        return router.createUrlTree(['/auth/login'], { queryParams: { returnUrl } });
      }

      return allowedRoles.length === 0 || allowedRoles.includes(utilisateur.role)
        ? true
        : router.createUrlTree([authService.homeRouteForRole(utilisateur.role)]);
    }),
  );
};
