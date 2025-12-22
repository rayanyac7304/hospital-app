import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-dossier-create',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, HttpClientModule],
  templateUrl: './dossier-create.html',
  styleUrls: ['./dossier-create.css']
})
export class DossierCreate implements OnInit {

  dossier = {
    title: '',
    patientId: null as number | null
  };

  patients: any[] = [];

  private dossierApi = 'http://localhost:8090/api/medical-records';
  private patientApi = 'http://localhost:8083/api/patients';

  constructor(
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef   // ✅ added
  ) {}

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.http.get<any>(this.patientApi).subscribe({
      next: res => {
        this.patients = res.content;
        this.cdr.detectChanges();   // ✅ force refresh
      },
      error: err => {
        console.error('Failed to load patients', err);
        this.cdr.detectChanges();
      }
    });
  }

  save(): void {
    if (!this.dossier.patientId) {
      alert('Veuillez sélectionner un patient');
      return;
    }

    this.http.post(this.dossierApi, this.dossier).subscribe({
      next: () => {
        alert('Dossier créé');
        this.router.navigate(['/dossier']);
      },
      error: err => {
        console.error('Creation failed', err);
        alert('Erreur lors de la création');
      }
    });
  }
}
