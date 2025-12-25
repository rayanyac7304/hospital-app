import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-facture-create',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './facture-create.html',
  styleUrls: ['./facture-create.css']
})
export class FactureCreateComponent implements OnInit {

  appointments: any[] = [];

  facture = {
    appointmentId: 0,
    amount: 0,
    description: ''
  };

  private appointmentsUrl = 'http://localhost:8084/api/appointments';
  private billsUrl = 'http://localhost:8085/api/bills';

  constructor(
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef   // 👈 ADD THIS
  ) {}

  ngOnInit(): void {
    this.http.get<any[]>(this.appointmentsUrl).subscribe({
      next: data => {
        this.appointments = data;

        // 👇 Force Angular to refresh the view
        this.cdr.detectChanges();
      },
      error: err => console.error('Failed to load appointments', err)
    });
  }

  save(): void {
    if (!this.facture.appointmentId) {
      alert('Veuillez sélectionner un rendez-vous');
      return;
    }

    this.http.post(this.billsUrl, this.facture).subscribe({
      next: () => this.router.navigate(['/facture']),
      error: err => console.error('Failed to create bill', err)
    });
  }
}
