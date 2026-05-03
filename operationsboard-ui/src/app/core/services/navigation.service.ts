import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SidebarTeamDto } from '../models/api.models';

@Injectable({
  providedIn: 'root',
})
export class NavigationService {
  private readonly baseUrl = '/api/navigation';

  constructor(private http: HttpClient) {}

  getSidebarTeams() {
    return this.http.get<SidebarTeamDto[]>(`${this.baseUrl}/sidebar`);
  }
}
