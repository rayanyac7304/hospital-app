import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-account-create',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
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
    // Admin check (UX only)
    const userRole = localStorage.getItem('role');
    if (userRole !== 'ADMIN') {
      this.router.navigate(['/']);
      return;
    }

    // Form definition
    this.accountForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['DOCTOR', Validators.required],

      // shared
      firstName: [''],
      lastName: [''],
      phone: [''],
      address: [''],

      // patient
      gender: [''],
      birthDate: [''],

      // doctor
      specialty: ['']
    });


    const form = this.accountForm;

    // Dynamic validators for PATIENT
    this.accountForm.get('role')!.valueChanges.subscribe(role => {

      const patientFields = ['firstName', 'lastName', 'gender', 'birthDate'];
      const doctorFields  = ['firstName', 'lastName', 'specialty'];

      [...patientFields, ...doctorFields].forEach(f => {
        const c = this.accountForm.get(f)!;
        c.clearValidators();
        c.updateValueAndValidity();
      });

      if (role === 'PATIENT') {
        patientFields.forEach(f => {
          const c = this.accountForm.get(f)!;
          c.setValidators(Validators.required);
          c.updateValueAndValidity();
        });
      }

      if (role === 'DOCTOR') {
        doctorFields.forEach(f => {
          const c = this.accountForm.get(f)!;
          c.setValidators(Validators.required);
          c.updateValueAndValidity();
        });
      }
    });

  }

  onSubmit(): void {
    console.log('SUBMIT CLICKED');

    if (this.accountForm.invalid) {
      console.log('FORM INVALID', this.accountForm.value);
      this.accountForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;

    const registerRequest = this.accountForm.value;

    this.http.post(`${this.apiUrl}/register`, registerRequest).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = 'Compte créé avec succès !';
        this.accountForm.reset({ role: 'DOCTOR' });
      },
      error: (error: any) => {
        this.isLoading = false;
        this.errorMessage = error?.error?.message || 'Erreur serveur';
      }
    });
  }


  onCancel(): void {
    this.router.navigate(['/accounts']);
  }
}
