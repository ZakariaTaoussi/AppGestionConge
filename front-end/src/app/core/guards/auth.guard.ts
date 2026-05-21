import { isPlatformBrowser } from '@angular/common';
import { inject } from '@angular/core';
import { PLATFORM_ID } from '@angular/core';
import { CanActivateFn, CanMatchFn, Router } from '@angular/router';
import { Role } from '../enums/role.enum';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (_route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!isPlatformBrowser(inject(PLATFORM_ID))) {
    return true;
  }

  if (authService.isAuthenticated()) {
    return true;
  }

  return router.createUrlTree(['/auth/login'], {
    queryParams: { returnUrl: state.url },
  });
};

export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!isPlatformBrowser(inject(PLATFORM_ID))) {
    return true;
  }

  if (!authService.isAuthenticated()) {
    return true;
  }

  return router.parseUrl(authService.homeRouteForRole());
};

export const roleGuard: CanMatchFn = route => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const roles = route.data?.['roles'] as Role[] | undefined;

  if (!isPlatformBrowser(inject(PLATFORM_ID))) {
    return true;
  }

  if (!authService.isAuthenticated()) {
    return router.parseUrl('/auth/login');
  }

  if (roles?.length && !authService.hasRole(roles)) {
    return router.parseUrl(authService.homeRouteForRole());
  }

  return true;
};
