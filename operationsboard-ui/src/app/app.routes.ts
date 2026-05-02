import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

import { Login } from './features/auth/login/login';
import { AppShell } from './features/layout/app-shell/app-shell';

import { MemberDashboard } from './features/dashboard/member-dashboard/member-dashboard';
import { TeamDashboard } from './features/dashboard/team-dashboard/team-dashboard';
import { ExecutiveDashboard } from './features/dashboard/executive-dashboard/executive-dashboard';
import { ExecutiveDrilldown } from './features/dashboard/executive-drilldown/executive-drilldown';

import { KanbanBoard } from './features/tasks/kanban-board/kanban-board';
import { TaskTree } from './features/tasks/task-tree/task-tree';

export const routes: Routes = [
  {
    path: 'login',
    component: Login,
  },
  {
    path: '',
    component: AppShell,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard/me',
      },
      {
        path: 'dashboard/me',
        component: MemberDashboard,
      },
      {
        path: 'dashboard/team/:teamId',
        component: TeamDashboard,
      },
      {
        path: 'dashboard/executive',
        component: ExecutiveDashboard,
      },
      {
        path: 'dashboard/executive/drilldown',
        component: ExecutiveDrilldown,
      },
      {
        path: 'team/:teamId/board',
        component: KanbanBoard,
      },
      {
        path: 'team/:teamId/tree',
        component: TaskTree,
      },
    ],
  },
  {
    path: '**',
    redirectTo: '',
  },
];
