import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export const apiCredentialsInterceptor: HttpInterceptorFn = (req, next) => {
  const apiRequest = req.url.startsWith(environment.apiUrl);
  return next(apiRequest ? req.clone({ withCredentials: true }) : req);
};
