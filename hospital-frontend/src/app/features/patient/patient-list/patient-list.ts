import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';  // ← ADD
import { HttpClient, HttpParams } from '@angular/common/http';  // ← ADD HttpParams
import { PatientService } from '../patient.service';

@Component({
  selector: 'app-patient-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],  // ← ADD FormsModule
  templateUrl: './patient-list.html',
  styleUrls: ['./patient-list.css']
})
export class PatientListComponent implements OnInit {

  patients: any[] = [];
  empty = false;
  searchQuery = '';  // ← ADD THIS
  private apiUrl = 'http://localhost:8083/api/patients';  // ← ADD THIS

  constructor(
    private http: HttpClient,  // ← ADD THIS
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients() {
    let params = new HttpParams();

    if (this.searchQuery) {
      params = params.set('q', this.searchQuery);  // ← ADD search
    }

    this.http.get(this.apiUrl, { params }).subscribe({
      next: (data: any) => {
        console.log('Raw data from API:', data);
        this.patients = data.content || [];
        console.log('Extracted patients array:', this.patients);
        this.empty = this.patients.length === 0;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading patients', err);
        this.empty = true;
        this.cdr.detectChanges();
      }
    });
  }
}
