import { Routes } from '@angular/router';

export const routes: Routes = [

  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },

  {
    path: 'login',
    loadComponent: () =>
      import('./features/login/login')
        .then(m => m.LoginComponent)
  },

  {
    path: 'accounts',
    loadChildren: () =>
      import('./features/accounts/accounts-routing-module')
        .then(m => m.AccountsRoutingModule)
  },

  {
    path: 'rdv',
    loadChildren: () =>
      import('./features/rdv/rdv-routing-module')
        .then(m => m.RdvRoutingModule)
  },

  {
    path: 'facture',
    loadChildren: () =>
      import('./features/facture/facture-routing-module')
        .then(m => m.FactureRoutingModule)
  },

  {
    path: 'patient',
    loadChildren: () =>
      import('./features/patient/patient-routing-module')
        .then(m => m.PatientRoutingModule)
  },

  {
    path: 'dossier',
    loadChildren: () =>
      import('./features/dossier/dossier-routing-module')
        .then(m => m.DossierRoutingModule)
  },


];
