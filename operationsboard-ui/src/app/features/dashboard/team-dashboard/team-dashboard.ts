import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DashboardService } from '../../../core/services/dashboard.service';
import { TeamDashboardDto } from '../../../core/models/api.models';

@Component({
  selector: 'app-team-dashboard',
  imports: [],
  templateUrl: './team-dashboard.html',
  styleUrl: './team-dashboard.scss',
})
export class TeamDashboard implements OnInit {
  dashboard = signal<TeamDashboardDto | null>(null);
  loading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(
    private route: ActivatedRoute,
    private dashboardService: DashboardService,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const teamId = Number(params.get('teamId'));

      this.loading.set(true);
      this.errorMessage.set(null);

      this.dashboardService.getTeamDashboard(teamId).subscribe({
        next: (dashboard) => {
          this.dashboard.set(dashboard);
          this.loading.set(false);
        },
        error: () => {
          this.errorMessage.set('Unable to load team dashboard.');
          this.loading.set(false);
        },
      });
    });
  }
}
