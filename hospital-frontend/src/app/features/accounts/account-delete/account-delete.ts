import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-account-delete',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './account-delete.html',
  styleUrls: ['./account-delete.css']
})
export class AccountDeleteComponent implements OnInit {

  userId!: number;
  username: string = '';

  private apiUrl = 'http://localhost:8087/api/auth';

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    const role = localStorage.getItem('role');
    if (role !== 'ADMIN') {
      this.router.navigate(['/']);
      return;
    }

    // Get id & username from route
    this.userId = Number(this.route.snapshot.paramMap.get('id'));
    this.username = this.route.snapshot.queryParamMap.get('username') || '';
  }

  confirmDelete(): void {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.delete<{ message: string }>(
      `${this.apiUrl}/users/${this.userId}`,
      { headers }
    ).subscribe({
      next: () => {
        alert('Compte supprimé avec succès');
        this.router.navigate(['/accounts']);
      },
      error: (error) => {
        console.error('Erreur suppression:', error);
        alert('Erreur lors de la suppression');
      }
    });
  }
}
