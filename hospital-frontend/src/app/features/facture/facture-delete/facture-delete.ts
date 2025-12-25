import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-facture-delete',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './facture-delete.html',   // ✅ CORRECT TEMPLATE
  styleUrls: ['./facture-delete.css']
})
export class FactureDeleteComponent implements OnInit {

  id!: number;

  private apiUrl = 'http://localhost:8088/api/bills'; // adjust port if needed

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
  }

  delete(): void {
    this.http.delete(`${this.apiUrl}/${this.id}`).subscribe({
      next: () => this.router.navigate(['/facture']),
      error: err => console.error('Delete failed', err)
    });
  }
}
