import { Component, OnInit, signal } from '@angular/core';
import { DashboardService } from '../../../core/services/dashboard.service';
import { MemberDashboardDto } from '../../../core/models/api.models';

@Component({
  selector: 'app-member-dashboard',
  imports: [],
  templateUrl: './member-dashboard.html',
  styleUrl: './member-dashboard.scss'
})
export class MemberDashboard implements OnInit {

  dashboard = signal<MemberDashboardDto | null>(null);
  loading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.loading.set(true);

    this.dashboardService.getMemberDashboard().subscribe({
      next: dashboard => {
        this.dashboard.set(dashboard);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Unable to load dashboard.');
        this.loading.set(false);
      }
    });
  }
}
