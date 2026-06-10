import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export const apiCredentialsInterceptor: HttpInterceptorFn = (request, next) => {
  const isApiRequest = request.url.startsWith(environment.apiUrl) || request.url.startsWith('/api');

  return next(isApiRequest ? request.clone({ withCredentials: true }) : request);
};
