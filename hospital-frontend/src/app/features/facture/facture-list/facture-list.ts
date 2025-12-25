import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient, HttpParams } from '@angular/common/http';

@Component({
  selector: 'app-facture-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './facture-list.html',
  styleUrls: ['./facture-list.css']
})
export class FactureListComponent implements OnInit {

  role: 'admin' | 'doctor' | 'patient' = 'admin'; // temporary

  factures: any[] = [];
  loading = false;
  error = '';

  search = '';
  date: string | null = null;

  private billsUrl = 'http://localhost:8085/api/bills';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadFactures();
  }

  loadFactures(): void {
    this.loading = true;
    this.error = '';

    let params = new HttpParams();

    if (this.search) {
      params = params.set('q', this.search);
    }

    // (Optional) date filtering later on backend
    if (this.date) {
      params = params.set('date', this.date);
    }

    this.http.get<any[]>(this.billsUrl, { params }).subscribe({
      next: data => {
        this.factures = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: err => {
        console.error('❌ Error loading bills:', err);
        this.error = 'Erreur lors du chargement des factures';
        this.factures = [];
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onSearchChange(value: string) {
    this.search = value;

    clearTimeout((this as any).searchTimeout);
    (this as any).searchTimeout = setTimeout(() => {
      this.loadFactures();
    }, 300);
  }

  onDateChange(value: string) {
    this.date = value || null;
    this.loadFactures();
  }

  deleteFacture(id: number): void {
    if (!confirm('Supprimer cette facture ?')) return;

    this.http.delete(`${this.billsUrl}/${id}`).subscribe({
      next: () => this.loadFactures(),
      error: err => console.error(err)
    });
  }
}
