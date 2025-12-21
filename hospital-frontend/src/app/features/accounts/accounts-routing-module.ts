import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./admin-list/admin-list')
        .then(m => m.AdminListComponent)
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./login/login')
        .then(m => m.LoginPage)
  },
  {
    path: 'create',
    loadComponent: () =>
      import('./account-create/account-create')
        .then(m => m.AccountCreateComponent)
  },

{
    path: 'edit/:id',
    loadComponent: () =>
      import('./account-edit/account-edit')
        .then(m => m.AccountEditPage)
  },

  {
    path: 'delete/:id',
    loadComponent: () =>
      import('./account-delete/account-delete')
        .then(m => m.AccountDeleteComponent)
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AccountsRoutingModule {}
