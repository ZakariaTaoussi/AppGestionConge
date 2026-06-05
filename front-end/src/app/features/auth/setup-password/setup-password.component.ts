import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-setup-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './setup-password.component.html',
  styleUrls: ['./setup-password.component.scss'],
})
export class SetupPasswordComponent {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  private readonly token = this.route.snapshot.queryParamMap.get('token')?.trim() ?? '';

  readonly isSubmitting = signal(false);
  readonly showPassword = signal(false);
  readonly showConfirmPassword = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');
  readonly hasToken = computed(() => this.token.length > 0);

  readonly setupPasswordForm = this.fb.nonNullable.group({
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', Validators.required],
  });

  onSubmit(): void {
    if (!this.hasToken()) {
      this.errorMessage.set('Le lien de configuration du mot de passe est invalide.');
      return;
    }

    if (this.setupPasswordForm.invalid) {
      this.setupPasswordForm.markAllAsTouched();
      return;
    }

    const { password, confirmPassword } = this.setupPasswordForm.getRawValue();
    if (password !== confirmPassword) {
      this.errorMessage.set('Les deux mots de passe ne correspondent pas.');
      return;
    }

    this.errorMessage.set('');
    this.successMessage.set('');
    this.isSubmitting.set(true);

    this.authService
      .setupPassword({ token: this.token, password })
      .pipe(finalize(() => this.isSubmitting.set(false)))
      .subscribe({
        next: () => {
          this.successMessage.set('Votre mot de passe a ete cree. Redirection vers la connexion...');
          setTimeout(() => this.router.navigateByUrl('/auth/login'), 1200);
        },
        error: error => {
          const message = typeof error?.error?.message === 'string'
            ? error.error.message
            : 'Impossible de configurer le mot de passe.';
          this.errorMessage.set(message);
        },
      });
  }

  togglePasswordVisibility(): void {
    this.showPassword.update(value => !value);
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword.update(value => !value);
  }
}
