import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  DashboardSummaryDto,
  ExecutiveDashboardDto,
  ExecutiveDrilldownDto,
  MemberDashboardDto,
  TeamDashboardDto,
} from '../models/api.models';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private readonly baseUrl = '/api/dashboard';

  constructor(private http: HttpClient) {}

  getMemberDashboard() {
    return this.http.get<MemberDashboardDto>(`${this.baseUrl}/me`);
  }

  getTeamDashboard(teamId: number) {
    return this.http.get<TeamDashboardDto>(`${this.baseUrl}/team/${teamId}`);
  }

  getExecutiveDashboard() {
    return this.http.get<ExecutiveDashboardDto>(`${this.baseUrl}/executive`);
  }

  getExecutiveDrilldown(type: string) {
    return this.http.get<ExecutiveDrilldownDto>(`${this.baseUrl}/executive/drilldown`, {
      params: { type },
    });
  }

  getDashboardSummary() {
    return this.http.get<DashboardSummaryDto>(`${this.baseUrl}/summary`);
  }
}
