import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DashboardService } from '../../../core/services/dashboard.service';
import { ExecutiveDrilldownDto, TaskDto } from '../../../core/models/api.models';
import { TaskDetail } from '../../tasks/task-detail/task-detail';

@Component({
  selector: 'app-executive-drilldown',
  standalone: true,
  imports: [CommonModule, TaskDetail],
  templateUrl: './executive-drilldown.html',
  styleUrl: './executive-drilldown.scss',
})
export class ExecutiveDrilldown implements OnInit {
  drilldown = signal<ExecutiveDrilldownDto | null>(null);
  loading = signal(true);
  errorMessage = signal<string | null>(null);
  selectedTaskId = signal<number | null>(null);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dashboardService: DashboardService,
  ) {}

  ngOnInit(): void {
    const type = this.route.snapshot.queryParamMap.get('type') || 'active';
    this.loadDrilldown(type);
  }

  loadDrilldown(type: string): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.dashboardService.getExecutiveDrilldown(type).subscribe({
      next: (data) => {
        this.drilldown.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Unable to load executive drilldown.');
        this.loading.set(false);
      },
    });
  }

  backToDashboard(): void {
    this.router.navigate(['/dashboard/executive']);
  }

  openTask(task: TaskDto): void {
    this.selectedTaskId.set(task.id);
  }

  closeTask(): void {
    this.selectedTaskId.set(null);
  }

  goToTeamBoard(task: TaskDto, event: MouseEvent): void {
    event.stopPropagation();
    this.router.navigate(['/team', task.teamId, 'board']);
  }

  statusClass(status: string): string {
    return status.toLowerCase();
  }

  priorityClass(priority: string): string {
    return priority.toLowerCase();
  }

  assignedName(task: TaskDto): string {
    return task.assignedUser
      ? `${task.assignedUser.firstName} ${task.assignedUser.lastName}`
      : 'Unassigned';
  }
}
