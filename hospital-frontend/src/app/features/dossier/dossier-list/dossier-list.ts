import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-dossier-list',
  standalone: true,
  imports: [CommonModule, RouterModule, HttpClientModule],
  templateUrl: './dossier-list.html',
  styleUrls: ['./dossier-list.css']
})
export class DossierList implements OnInit {

  dossiers: any[] = [];
  private apiUrl = 'http://localhost:8090/api/medical-records';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadDossiers();
  }

  loadDossiers(): void {
    this.http.get<any[]>(this.apiUrl).subscribe({
      next: data => {
        this.dossiers = data;
        this.cdr.detectChanges(); // 🔹 force Angular to update the view
      },
      error: err => console.error('Failed to load dossiers', err)
    });
  }
}
