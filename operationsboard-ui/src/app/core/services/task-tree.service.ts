import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TaskTreeDto } from '../models/api.models';

@Injectable({
  providedIn: 'root',
})
export class TaskTreeService {
  private readonly baseUrl = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) {}

  getTeamTaskTree(teamId: number): Observable<TaskTreeDto> {
    return this.http.get<TaskTreeDto>(`${this.baseUrl}/team/${teamId}/tree`);
  }
}
