import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';  // ← ADD
import { HttpClient, HttpParams } from '@angular/common/http';  // ← ADD

@Component({
  selector: 'app-rdv-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],  // ← ADD FormsModule
  templateUrl: './rdv-list.html',
  styleUrls: ['./rdv-list.css']
})
export class RdvListPage implements OnInit {

  rdvs: any[] = [];
  empty = false;
  searchQuery = '';  // ← ADD
  filterDate = '';  // ← ADD
  private apiUrl = 'http://localhost:8084/api/appointments';  // ← ADD (adjust port)

  constructor(
    private http: HttpClient,  // ← CHANGE to HttpClient
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadRdvs();
  }

  loadRdvs() {
    let params = new HttpParams();

    // Add date filter if selected
    if (this.filterDate) {
      params = params.set('date', this.filterDate);
    }

    this.http.get(this.apiUrl, { params }).subscribe({
      next: (data: any) => {
        console.log("Raw RDV list from backend:", data);

        // Client-side search filter (by patient or doctor name)
        if (this.searchQuery) {
          const query = this.searchQuery.toLowerCase();
          this.rdvs = data.filter((r: any) =>
            r.patientName?.toLowerCase().includes(query) ||
            r.doctorName?.toLowerCase().includes(query)
          );
        } else {
          this.rdvs = data || [];
        }

        this.empty = this.rdvs.length === 0;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading RDVs:', err);
        this.empty = true;
        this.cdr.detectChanges();
      }
    });
  }

  goEdit(id: number) {
    this.router.navigate(['/rdv/edit', id]);
  }

  goCancel(id: number) {
    this.router.navigate(['/rdv/cancel', id]);
  }

  goDelete(id: number) {
    this.router.navigate(['/rdv/delete', id]);
  }
}
