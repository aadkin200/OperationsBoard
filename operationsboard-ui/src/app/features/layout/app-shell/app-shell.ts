import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NavigationService } from '../../../core/services/navigation.service';
import { SidebarTeamDto } from '../../../core/models/api.models';

@Component({
  selector: 'app-app-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app-shell.html',
  styleUrl: './app-shell.scss',
})
export class AppShell implements OnInit {
  teams = signal<SidebarTeamDto[]>([]);
  teamsExpanded = signal(false);
  expandedTeamIds = signal<Set<number>>(new Set<number>());

  constructor(
    public authService: AuthService,
    private navigationService: NavigationService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.navigationService.getSidebarTeams().subscribe({
      next: (teams) => this.teams.set(teams),
      error: () => this.teams.set([]),
    });
  }

  canViewExecutiveDashboard(): boolean {
    const user = this.authService.currentUser();

    return user?.globalAccessLevel === 'EXECUTIVE' || user?.globalAccessLevel === 'SUPER_USER';
  }

  toggleTeams(): void {
    this.teamsExpanded.set(!this.teamsExpanded());
  }

  toggleTeam(teamId: number): void {
    const next = new Set(this.expandedTeamIds());

    if (next.has(teamId)) {
      next.delete(teamId);
    } else {
      next.add(teamId);
    }

    this.expandedTeamIds.set(next);
  }

  isTeamExpanded(teamId: number): boolean {
    return this.expandedTeamIds().has(teamId);
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.router.navigate(['/login']),
    });
  }
}
