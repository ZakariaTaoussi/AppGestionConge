import { Observable } from 'rxjs';
import { LoginRequest, LoginResponse, SetupPasswordRequest } from '../models/auth.model';
import { Utilisateur } from '../models/utilisateur.model';

export interface IAuthService {
  login(request: LoginRequest): Observable<LoginResponse>;

  logout(): Observable<void>;

  me(): Observable<Utilisateur>;

  setupPassword(request: SetupPasswordRequest): Observable<void>;
}
