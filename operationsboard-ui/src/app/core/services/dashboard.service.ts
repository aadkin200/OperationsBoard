import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  DashboardSummaryDto,
  MemberDashboardDto,
  TeamDashboardDto
} from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly baseUrl = 'http://localhost:8080/api/dashboard';

  constructor(private http: HttpClient) {}

  getMemberDashboard() {
    return this.http.get<MemberDashboardDto>(`${this.baseUrl}/me`);
  }

  getTeamDashboard(teamId: number) {
    return this.http.get<TeamDashboardDto>(`${this.baseUrl}/team/${teamId}`);
  }

  getExecutiveDashboard() {
    return this.http.get<DashboardSummaryDto>(`${this.baseUrl}/executive`);
  }
}
