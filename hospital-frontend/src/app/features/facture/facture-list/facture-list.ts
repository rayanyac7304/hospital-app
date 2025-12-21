import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-facture-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './facture-list.html',
  styleUrls: ['./facture-list.css']
})
export class FactureListComponent {

  role: 'admin' | 'doctor' | 'patient' = 'admin'; // temporary

  factures: any[] = [];
}
