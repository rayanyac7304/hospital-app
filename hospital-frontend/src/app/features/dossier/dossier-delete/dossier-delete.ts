import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-dossier-delete',
  standalone: true,
  imports: [CommonModule, RouterModule, HttpClientModule],
  templateUrl: './dossier-delete.html',
  styleUrls: ['./dossier-delete.css']
})
export class DossierDelete {

  id!: number;
  private apiUrl = 'http://localhost:8090/api/medical-records';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
  }

  confirmDelete(): void {
    if (!confirm('Confirmer la suppression du dossier ?')) {
      return;
    }

    this.http.delete(`${this.apiUrl}/${this.id}`).subscribe({
      next: () => {
        alert('Dossier supprimé');
        this.router.navigate(['/dossier']);
      },
      error: err => {
        console.error('Delete failed', err);
        alert('Erreur lors de la suppression');
      }
    });
  }
}
