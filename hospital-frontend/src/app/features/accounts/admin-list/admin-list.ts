import { Component, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-accounts-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-list.html',
  styleUrls: ['./admin-list.css']
})
export class AdminListComponent implements AfterViewInit {
  accounts: any[] = [];
  filteredAccounts: any[] = [];

  private apiUrl = 'http://localhost:8087/api/auth';

  constructor(
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngAfterViewInit(): void {
    const userRole = localStorage.getItem('role');
    if (userRole !== 'ADMIN') {
      this.router.navigate(['/']);
      return;
    }

    this.loadAccounts();
  }

  loadAccounts(): void {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<any[]>(`${this.apiUrl}/users`, { headers }).subscribe({
      next: (data) => {
        // Filter out admin accounts
        this.accounts = data.filter(acc => acc.role !== 'ADMIN');
        this.filteredAccounts = [...this.accounts];
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error loading accounts:', error);
        this.cdr.detectChanges();
      }
    });
  }

  onSearch(event: Event): void {
    const input = event.target as HTMLInputElement;
    const searchTerm = input.value.toLowerCase();

    if (searchTerm) {
      this.filteredAccounts = this.accounts.filter(acc =>
        acc.username.toLowerCase().includes(searchTerm) ||
        acc.email.toLowerCase().includes(searchTerm)
      );
    } else {
      this.filteredAccounts = [...this.accounts];
    }
    this.cdr.detectChanges();
  }

  getRoleLabel(role: string): string {
    const roleLabels: { [key: string]: string } = {
      'DOCTOR': 'Médecin',
      'PATIENT': 'Patient'
    };
    return roleLabels[role] || role;
  }
}
