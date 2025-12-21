import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class Navbar implements OnInit {
  role: string = '';

  ngOnInit(): void {
    this.role = (localStorage.getItem('role') || '').toLowerCase();
  }

  get visibleNavItems() {
    const allItems = [
      { path: '/accounts', label: 'Comptes', roles: ['admin'] },
      { path: '/patient', label: 'Patients', roles: ['admin', 'doctor'] },
      { path: '/rdv', label: 'Rendez-vous', roles: ['admin', 'doctor', 'patient'] },
      { path: '/facture', label: 'Factures', roles: ['admin', 'doctor'] },
      { path: '/dossier', label: 'Dossiers', roles: ['admin', 'doctor'] },
      { path: '/account', label: 'Compte', roles: ['doctor', 'patient'] }
    ];

    return allItems.filter(item => item.roles.includes(this.role));
  }
}
