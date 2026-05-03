import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TeamDto } from '../models/api.models';

@Injectable({
  providedIn: 'root',
})
export class TeamService {
  private readonly baseUrl = '/api/teams';

  constructor(private http: HttpClient) {}

  getTeams() {
    return this.http.get<TeamDto[]>(this.baseUrl);
  }
}
