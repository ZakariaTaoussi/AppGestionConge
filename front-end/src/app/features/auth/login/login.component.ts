import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly isSubmitting = signal(false);
  readonly showPassword = signal(false);
  readonly errorMessage = signal('');

  readonly loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
    remember: [false],
  });

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.errorMessage.set('');
    this.isSubmitting.set(true);

    this.authService
      .login(this.loginForm.getRawValue())
      .pipe(finalize(() => this.isSubmitting.set(false)))
      .subscribe({
        next: response => {
          const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
          const targetUrl = this.isInternalReturnUrl(returnUrl)
            ? returnUrl
            : this.authService.homeRouteForRole(response.utilisateur.role);

          this.router.navigateByUrl(targetUrl);
        },
        error: () => {
          this.errorMessage.set('Identifiants invalides ou serveur indisponible.');
        },
      });
  }

  togglePasswordVisibility(): void {
    this.showPassword.update(value => !value);
  }

  private isInternalReturnUrl(url: string | null): url is string {
    return !!url && url.startsWith('/') && !url.startsWith('//') && !url.startsWith('/auth');
  }
}
