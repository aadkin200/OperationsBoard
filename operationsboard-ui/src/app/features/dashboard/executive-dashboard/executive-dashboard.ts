import { Component, OnInit, signal } from '@angular/core';
import { DashboardService } from '../../../core/services/dashboard.service';
import { DashboardSummaryDto } from '../../../core/models/api.models';

@Component({
  selector: 'app-executive-dashboard',
  imports: [],
  templateUrl: './executive-dashboard.html',
  styleUrl: './executive-dashboard.scss'
})
export class ExecutiveDashboard implements OnInit {

  dashboard = signal<DashboardSummaryDto | null>(null);
  loading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.loading.set(true);

    this.dashboardService.getExecutiveDashboard().subscribe({
      next: dashboard => {
        this.dashboard.set(dashboard);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Unable to load executive dashboard.');
        this.loading.set(false);
      }
    });
  }
}
