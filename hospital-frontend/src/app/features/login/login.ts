import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  isLoading = false;
  errorMessage = '';

  private apiUrl = 'http://localhost:8087/api/auth';

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const loginRequest = {
      usernameOrEmail: this.loginForm.value.username,
      password: this.loginForm.value.password
    };

    this.http.post<any>(`${this.apiUrl}/login`, loginRequest).subscribe({
      next: (response) => {
        console.log('Login successful:', response);
        this.isLoading = false;
       if (response.accessToken) {
         localStorage.setItem('token', response.accessToken);
         localStorage.setItem('role', response.role);
         localStorage.setItem('username', response.username);
       }

        // Role-based redirect
        const role = response.role;
        if (role === 'DOCTOR') {
          this.router.navigate(['/patient']);
        } else if (role === 'ADMIN') {
          this.router.navigate(['/accounts']);
        } else if (role === 'PATIENT') {
          this.router.navigate(['/rdv']);
        } else {
          this.router.navigate(['/patient']); // default fallback
        }
      },
      error: (error) => {
        console.error('Login error:', error);
        console.error('Error status:', error.status);
        console.error('Error message:', error.message);
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Invalid username or password. Please try again.';
      }
    });
  }
}
