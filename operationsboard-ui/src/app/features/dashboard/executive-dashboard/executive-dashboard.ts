import {
  Component,
  OnInit,
  OnDestroy,
  signal,
  AfterViewInit,
  ElementRef,
  ViewChildren,
  QueryList
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../../core/services/dashboard.service';
import { ReportService } from '../../../core/services/report.service';
import {
  ExecutiveDashboardDto,
  TeamHealthDto,
  EscalationItemDto,
  HealthStatus
} from '../../../core/models/api.models';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-executive-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './executive-dashboard.html',
  styleUrl: './executive-dashboard.scss'
})
export class ExecutiveDashboard implements OnInit, AfterViewInit, OnDestroy {

  @ViewChildren('chartCanvas') chartCanvases!: QueryList<ElementRef<HTMLCanvasElement>>;

  dashboard = signal<ExecutiveDashboardDto | null>(null);
  loading = signal(true);
  errorMessage = signal<string | null>(null);
  downloadingExcel = signal(false);
  downloadingPdf = signal(false);
  today = new Date();

  private charts: Chart[] = [];
  private dataReady = false;
  private viewReady = false;

  constructor(
    private dashboardService: DashboardService,
    private reportService: ReportService
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
      }
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

  private destroyCharts(): void {
    this.charts.forEach(c => c.destroy());
    this.charts = [];
  }

  private buildCharts(): void {
    const data = this.dashboard();
    if (!data) return;

    this.destroyCharts();

    const canvases = this.chartCanvases.toArray();

    const workloadCanvas = canvases.find(el => el.nativeElement.id === 'workloadChart');
    const trendCanvas = canvases.find(el => el.nativeElement.id === 'trendChart');
    const completedCanvas = canvases.find(el => el.nativeElement.id === 'completedChart');
    const unassignedCanvas = canvases.find(el => el.nativeElement.id === 'unassignedChart');

    if (workloadCanvas) {
      this.charts.push(this.buildHorizontalBar(
        workloadCanvas.nativeElement,
        data.workloadByTeam.map(t => t.teamName),
        data.workloadByTeam.map(t => t.count),
        '#3b82f6',
        'Active Tasks'
      ));
    }

    if (trendCanvas && data.completionTrend.length > 0) {
      this.charts.push(this.buildLineChart(
        trendCanvas.nativeElement,
        data.completionTrend.map(t => t.monthLabel),
        data.completionTrend.map(t => t.count)
      ));
    }

    if (completedCanvas) {
      this.charts.push(this.buildHorizontalBar(
        completedCanvas.nativeElement,
        data.completedByTeam.map(t => t.teamName),
        data.completedByTeam.map(t => t.count),
        '#10b981',
        'Completed This Month'
      ));
    }

    if (unassignedCanvas) {
      this.charts.push(this.buildHorizontalBar(
        unassignedCanvas.nativeElement,
        data.unassignedBacklogByTeam.map(t => t.teamName),
        data.unassignedBacklogByTeam.map(t => t.count),
        '#f59e0b',
        'Unassigned Tasks'
      ));
    }
  }

  private buildHorizontalBar(
    canvas: HTMLCanvasElement,
    labels: string[],
    values: number[],
    color: string,
    label: string
  ): Chart {
    return new Chart(canvas, {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          label,
          data: values,
          backgroundColor: color + 'cc',
          borderColor: color,
          borderWidth: 1,
          borderRadius: 6
        }]
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false }
        },
        scales: {
          x: {
            beginAtZero: true,
            ticks: { stepSize: 1, color: '#6b7280' },
            grid: { color: '#f3f4f6' }
          },
          y: {
            ticks: { color: '#374151', font: { size: 13 } },
            grid: { display: false }
          }
        }
      }
    });
  }

  private buildLineChart(
    canvas: HTMLCanvasElement,
    labels: string[],
    values: number[]
  ): Chart {
    return new Chart(canvas, {
      type: 'line',
      data: {
        labels,
        datasets: [{
          label: 'Completed',
          data: values,
          borderColor: '#6366f1',
          backgroundColor: '#6366f120',
          fill: true,
          tension: 0.4,
          pointBackgroundColor: '#6366f1',
          pointRadius: 5
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false }
        },
        scales: {
          x: {
            ticks: { color: '#6b7280' },
            grid: { color: '#f3f4f6' }
          },
          y: {
            beginAtZero: true,
            ticks: { stepSize: 1, color: '#6b7280' },
            grid: { color: '#f3f4f6' }
          }
        }
      }
    });
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

  isOverdue(dueDate: string | null): boolean {
    if (!dueDate) return false;
    return new Date(dueDate) < new Date();
  }

  get sortedEscalations(): EscalationItemDto[] {
    const data = this.dashboard();
    if (!data) return [];
    return [...data.escalationQueue].sort((a, b) => b.daysBlocked - a.daysBlocked);
  }

  get sortedTeamHealth(): TeamHealthDto[] {
    const data = this.dashboard();
    if (!data) return [];
    const order: Record<HealthStatus, number> = { RED: 0, YELLOW: 1, GREEN: 2 };
    return [...data.teamHealth].sort((a, b) => order[a.healthStatus] - order[b.healthStatus]);
  }

  downloadExcel(): void {
    if (this.downloadingExcel()) return;
    this.downloadingExcel.set(true);
    this.reportService.downloadExcelReport().subscribe({
      next: (blob) => {
        this.reportService.triggerDownload(blob, `executive-report-${this.formatDate()}.xlsx`);
        this.downloadingExcel.set(false);
      },
      error: () => this.downloadingExcel.set(false)
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
      error: () => this.downloadingPdf.set(false)
    });
  }

  private formatDate(): string {
    return this.today.toISOString().slice(0, 10);
  }
}
