import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  username = '';
  password = '';
  errorMessage = signal<string | null>(null);
  loading = signal(false);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login(): void {
    this.errorMessage.set(null);
    this.loading.set(true);

    this.authService.login(this.username, this.password).subscribe({
      next: () => {
        this.authService.loadCurrentUser().subscribe({
          next: () => {
            this.loading.set(false);
            this.router.navigate(['/dashboard/me']);
          },
          error: () => {
            this.loading.set(false);
            this.errorMessage.set('Login succeeded, but user profile could not be loaded.');
          }
        });
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set('Invalid username or password.');
      }
    });
  }
}
