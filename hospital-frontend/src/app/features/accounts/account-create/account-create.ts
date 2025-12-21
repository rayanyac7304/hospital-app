import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-account-create',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './account-create.html',
  styleUrls: ['./account-create.css']
})
export class AccountCreateComponent implements OnInit {
  accountForm!: FormGroup;
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  private apiUrl = 'http://localhost:8087/api/auth';

  roles = [
    { value: 'DOCTOR', label: 'Médecin' },
    { value: 'PATIENT', label: 'Patient' }
  ];

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Check if user is admin
    const userRole = localStorage.getItem('role');
    if (userRole !== 'ADMIN') {
      this.router.navigate(['/']);
      return;
    }

    this.accountForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['DOCTOR', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.accountForm.invalid) {
      this.accountForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const registerRequest = {
      username: this.accountForm.value.username,
      email: this.accountForm.value.email,
      password: this.accountForm.value.password,
      role: this.accountForm.value.role
    };

    this.http.post<any>(`${this.apiUrl}/register`, registerRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = 'Compte créé avec succès!';
        this.accountForm.reset({ role: 'DOCTOR' });

        // Redirect to accounts list after 2 seconds
        setTimeout(() => {
          this.router.navigate(['/accounts']);
        }, 2000);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Erreur lors de la création du compte. Veuillez réessayer.';
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/accounts']);
  }
}
