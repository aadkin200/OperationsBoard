import {
  Component,
  OnInit,
  OnDestroy,
  signal,
  AfterViewInit,
  ElementRef,
  ViewChildren,
  QueryList,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { DashboardService } from '../../../core/services/dashboard.service';
import { ReportService } from '../../../core/services/report.service';
import { TaskDetail } from '../../tasks/task-detail/task-detail';
import {
  ExecutiveDashboardDto,
  TeamHealthDto,
  EscalationItemDto,
  HealthStatus,
  TeamMetricDto,
  UserWorkloadDto,
  LeadershipActionDto,
} from '../../../core/models/api.models';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-executive-dashboard',
  standalone: true,
  imports: [CommonModule, TaskDetail],
  templateUrl: './executive-dashboard.html',
  styleUrl: './executive-dashboard.scss',
})
export class ExecutiveDashboard implements OnInit, AfterViewInit, OnDestroy {
  @ViewChildren('chartCanvas') chartCanvases!: QueryList<ElementRef<HTMLCanvasElement>>;

  dashboard = signal<ExecutiveDashboardDto | null>(null);
  loading = signal(true);
  errorMessage = signal<string | null>(null);
  downloadingExcel = signal(false);
  downloadingPdf = signal(false);
  selectedTaskId = signal<number | null>(null);
  today = new Date();

  private charts: Chart[] = [];
  private dataReady = false;
  private viewReady = false;

  constructor(
    private dashboardService: DashboardService,
    private reportService: ReportService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.dashboardService.getExecutiveDashboard().subscribe({
      next: (data) => {
        this.dashboard.set(data);
        this.loading.set(false);
        this.dataReady = true;
        if (this.viewReady) {
          setTimeout(() => this.buildCharts(), 0);
        }
      },
      error: () => {
        this.errorMessage.set('Unable to load executive dashboard.');
        this.loading.set(false);
      },
    });
  }

  ngAfterViewInit(): void {
    this.viewReady = true;
    if (this.dataReady) {
      setTimeout(() => this.buildCharts(), 0);
    }

    this.chartCanvases.changes.subscribe(() => {
      if (this.dataReady) {
        setTimeout(() => this.buildCharts(), 0);
      }
    });
  }

  ngOnDestroy(): void {
    this.destroyCharts();
  }

  openTaskDetail(taskId: number): void {
    this.selectedTaskId.set(taskId);
  }

  closeTaskDetail(): void {
    this.selectedTaskId.set(null);
  }

  goToTeamMetrics(teamId: number): void {
    this.router.navigate(['/dashboard/team', teamId]);
  }

  goToTeamBoard(teamId: number): void {
    this.router.navigate(['/team', teamId, 'board']);
  }

  private destroyCharts(): void {
    this.charts.forEach((c) => c.destroy());
    this.charts = [];
  }

  private buildCharts(): void {
    const data = this.dashboard();
    if (!data) return;

    this.destroyCharts();

    const canvases = this.chartCanvases.toArray();

    const workloadCanvas = canvases.find((el) => el.nativeElement.id === 'workloadChart');
    const trendCanvas = canvases.find((el) => el.nativeElement.id === 'trendChart');
    const completedCanvas = canvases.find((el) => el.nativeElement.id === 'completedChart');
    const unassignedCanvas = canvases.find((el) => el.nativeElement.id === 'unassignedChart');

    if (workloadCanvas) {
      this.charts.push(
        this.buildHorizontalBar(
          workloadCanvas.nativeElement,
          data.workloadByTeam.map((t) => t.teamName),
          data.workloadByTeam.map((t) => t.count),
          '#3b82f6',
          'Active Tasks',
        ),
      );
    }

    if (trendCanvas && data.completionTrend.length > 0) {
      this.charts.push(
        this.buildLineChart(
          trendCanvas.nativeElement,
          data.completionTrend.map((t) => t.monthLabel),
          data.completionTrend.map((t) => t.count),
        ),
      );
    }

    if (completedCanvas) {
      this.charts.push(
        this.buildHorizontalBar(
          completedCanvas.nativeElement,
          data.completedByTeam.map((t) => t.teamName),
          data.completedByTeam.map((t) => t.count),
          '#10b981',
          'Completed This Month',
        ),
      );
    }

    if (unassignedCanvas) {
      this.charts.push(
        this.buildHorizontalBar(
          unassignedCanvas.nativeElement,
          data.unassignedBacklogByTeam.map((t) => t.teamName),
          data.unassignedBacklogByTeam.map((t) => t.count),
          '#f59e0b',
          'Unassigned Tasks',
        ),
      );
    }
  }

  private buildHorizontalBar(
    canvas: HTMLCanvasElement,
    labels: string[],
    values: number[],
    color: string,
    label: string,
  ): Chart {
    return new Chart(canvas, {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label,
            data: values,
            backgroundColor: color + 'cc',
            borderColor: color,
            borderWidth: 1,
            borderRadius: 6,
          },
        ],
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
        },
        scales: {
          x: {
            beginAtZero: true,
            ticks: { stepSize: 1, color: '#6b7280' },
            grid: { color: '#f3f4f6' },
          },
          y: {
            ticks: { color: '#374151', font: { size: 13 } },
            grid: { display: false },
          },
        },
      },
    });
  }

  private buildLineChart(canvas: HTMLCanvasElement, labels: string[], values: number[]): Chart {
    return new Chart(canvas, {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            label: 'Completed',
            data: values,
            borderColor: '#6366f1',
            backgroundColor: '#6366f120',
            fill: true,
            tension: 0.4,
            pointBackgroundColor: '#6366f1',
            pointRadius: 5,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
        },
        scales: {
          x: {
            ticks: { color: '#6b7280' },
            grid: { color: '#f3f4f6' },
          },
          y: {
            beginAtZero: true,
            ticks: { stepSize: 1, color: '#6b7280' },
            grid: { color: '#f3f4f6' },
          },
        },
      },
    });
  }

  get readinessScore(): number {
    const data = this.dashboard();
    if (!data || data.totalActiveTasks === 0) return 100;

    let score = 100;
    score -= data.totalCriticalRisk * 12;
    score -= data.totalOverdue * 5;
    score -= data.totalBlocked * 4;
    score -= data.totalUnassigned * 2;
    score -= data.teamHealth.filter((t) => t.healthStatus === 'RED').length * 10;
    score -= data.teamHealth.filter((t) => t.healthStatus === 'YELLOW').length * 4;

    return Math.max(0, Math.min(100, score));
  }

  get readinessLevel(): 'critical' | 'watch' | 'ready' {
    if (this.readinessScore < 60) return 'critical';
    if (this.readinessScore < 80) return 'watch';
    return 'ready';
  }

  get readinessLabel(): string {
    if (this.readinessLevel === 'critical') return 'Mission Risk Elevated';
    if (this.readinessLevel === 'watch') return 'Operational Pressure Detected';
    return 'Operationally Ready';
  }

  get readinessNarrative(): string {
    const data = this.dashboard();
    if (!data) return '';

    if (this.readinessLevel === 'critical') {
      return 'Current workload, blockers, and overdue items indicate elevated organizational delivery risk.';
    }

    if (this.readinessLevel === 'watch') {
      return 'Teams are operating with measurable pressure. Leadership awareness is recommended.';
    }

    return 'No major organization-wide delivery risks are currently visible.';
  }

  get executiveDecisionItems(): LeadershipActionDto[] {
    const data = this.dashboard();
    if (!data) return [];

    return [...(data.leadershipActions ?? [])]
      .sort((a, b) => this.severityRank(a.severity) - this.severityRank(b.severity))
      .slice(0, 5);
  }

  get operationalSummaryCards(): {
    label: string;
    value: number;
    tone: string;
    caption: string;
    type: string;
  }[] {
    const data = this.dashboard();
    if (!data) return [];

    return [
      {
        label: 'Active Work',
        value: data.totalActiveTasks,
        tone: 'blue',
        caption: 'Total visible workload',
        type: 'active',
      },
      {
        label: 'Mission Risk',
        value: data.totalCriticalRisk,
        tone: data.totalCriticalRisk > 0 ? 'red' : 'neutral',
        caption: 'Critical priority risk items',
        type: 'mission-risk',
      },
      {
        label: 'Blocked',
        value: data.totalBlocked,
        tone: data.totalBlocked > 0 ? 'red' : 'neutral',
        caption: 'Work requiring unblock support',
        type: 'blocked',
      },
      {
        label: 'SLA Pressure',
        value: data.totalOverdue + data.totalDueSoon,
        tone: data.totalOverdue > 0 ? 'red' : data.totalDueSoon > 0 ? 'yellow' : 'neutral',
        caption: 'Overdue and due soon',
        type: 'sla-pressure',
      },
      {
        label: 'Unassigned',
        value: data.totalUnassigned,
        tone: data.totalUnassigned > 0 ? 'yellow' : 'neutral',
        caption: 'Backlog without owner',
        type: 'unassigned',
      },
      {
        label: 'Completed',
        value: data.completedThisMonth,
        tone: 'green',
        caption: 'Completed this month',
        type: 'completed',
      },
    ];
  }

  get sortedTeamHealth(): TeamHealthDto[] {
    const data = this.dashboard();
    if (!data) return [];
    const order: Record<HealthStatus, number> = { RED: 0, YELLOW: 1, GREEN: 2 };
    return [...data.teamHealth].sort((a, b) => {
      const healthDiff = order[a.healthStatus] - order[b.healthStatus];
      if (healthDiff !== 0) return healthDiff;
      return this.teamRiskScore(b) - this.teamRiskScore(a);
    });
  }

  get sortedEscalations(): EscalationItemDto[] {
    const data = this.dashboard();
    if (!data) return [];
    return [...data.escalationQueue].sort((a, b) => b.daysBlocked - a.daysBlocked);
  }

  get highestRiskTeam(): TeamHealthDto | null {
    const data = this.dashboard();
    if (!data || data.teamHealth.length === 0) return null;

    return [...data.teamHealth].sort((a, b) => {
      const scoreA = this.teamRiskScore(a);
      const scoreB = this.teamRiskScore(b);
      return scoreB - scoreA;
    })[0];
  }

  get highestWorkloadTeam(): TeamMetricDto | null {
    const data = this.dashboard();
    if (!data || data.workloadByTeam.length === 0) return null;

    return [...data.workloadByTeam].sort((a, b) => b.count - a.count)[0];
  }

  get mostBlockedTeam(): TeamMetricDto | null {
    const data = this.dashboard();
    if (!data || data.teamHealth.length === 0) return null;

    return (
      data.teamHealth
        .map((t) => ({
          teamId: t.teamId,
          teamName: t.teamName,
          count: t.blockedCount,
        }))
        .sort((a, b) => b.count - a.count)[0] ?? null
    );
  }

  get staffingPressurePeople(): UserWorkloadDto[] {
    const data = this.dashboard();
    if (!data) return [];

    return [...data.workloadByEmployee]
      .filter((p) => p.openTaskCount >= 5)
      .sort((a, b) => b.openTaskCount - a.openTaskCount)
      .slice(0, 5);
  }

  healthClass(status: HealthStatus): string {
    if (status === 'RED') return 'health-red';
    if (status === 'YELLOW') return 'health-yellow';
    return 'health-green';
  }

  healthLabel(status: HealthStatus): string {
    if (status === 'RED') return 'At Risk';
    if (status === 'YELLOW') return 'Watch';
    return 'Healthy';
  }

  priorityClass(priority: string): string {
    if (priority === 'CRITICAL') return 'priority-critical';
    if (priority === 'HIGH') return 'priority-high';
    return 'priority-normal';
  }

  pressureClass(value: number): string {
    if (value >= 10) return 'pressure-high';
    if (value >= 4) return 'pressure-medium';
    return 'pressure-low';
  }

  actionClass(severity: string): string {
    return severity.toLowerCase();
  }

  actionRecommendation(action: LeadershipActionDto): string {
    if (action.severity === 'CRITICAL') {
      return 'Recommendation: executive review or escalation may be required.';
    }

    if (action.category.toLowerCase().includes('staffing')) {
      return 'Recommendation: review team capacity and assignment balance.';
    }

    if (action.category.toLowerCase().includes('blocked')) {
      return 'Recommendation: identify external dependency or unblock owner.';
    }

    if (
      action.category.toLowerCase().includes('overdue') ||
      action.category.toLowerCase().includes('delivery')
    ) {
      return 'Recommendation: confirm delivery priority and recovery plan.';
    }

    return 'Recommendation: monitor until risk is reduced.';
  }

  isOverdue(dueDate: string | null): boolean {
    if (!dueDate) return false;
    return new Date(dueDate) < new Date();
  }

  private severityRank(severity: string): number {
    if (severity === 'CRITICAL') return 0;
    if (severity === 'HIGH') return 1;
    if (severity === 'MEDIUM') return 2;
    return 3;
  }

  private teamRiskScore(team: TeamHealthDto): number {
    return (
      team.overdueCount * 5 + team.blockedCount * 4 + team.unassignedCount * 2 + team.openCount
    );
  }

  downloadExcel(): void {
    if (this.downloadingExcel()) return;
    this.downloadingExcel.set(true);
    this.reportService.downloadExcelReport().subscribe({
      next: (blob) => {
        this.reportService.triggerDownload(blob, `executive-report-${this.formatDate()}.xlsx`);
        this.downloadingExcel.set(false);
      },
      error: () => this.downloadingExcel.set(false),
    });
  }

  downloadPdf(): void {
    if (this.downloadingPdf()) return;
    this.downloadingPdf.set(true);
    this.reportService.downloadPdfReport().subscribe({
      next: (blob) => {
        this.reportService.triggerDownload(blob, `executive-report-${this.formatDate()}.pdf`);
        this.downloadingPdf.set(false);
      },
      error: () => this.downloadingPdf.set(false),
    });
  }

  private formatDate(): string {
    return this.today.toISOString().slice(0, 10);
  }

  goToExecutiveDrilldown(type: string): void {
    this.router.navigate(['/dashboard/executive/drilldown'], {
      queryParams: { type },
    });
  }
}
