import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TaskBoardDto, TaskDto, TaskStatus } from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private readonly baseUrl = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) {}

  getTeamBoard(teamId: number): Observable<TaskBoardDto> {
    return this.http.get<TaskBoardDto>(`${this.baseUrl}/team/${teamId}/board`);
  }

  claimTask(taskId: number): Observable<TaskDto> {
    return this.http.patch<TaskDto>(`${this.baseUrl}/${taskId}/claim`, {});
  }

  unclaimTask(taskId: number): Observable<TaskDto> {
    return this.http.patch<TaskDto>(`${this.baseUrl}/${taskId}/unclaim`, {});
  }

  updateStatus(taskId: number, status: TaskStatus, blockerReason?: string): Observable<TaskDto> {
    return this.http.patch<TaskDto>(`${this.baseUrl}/${taskId}/status`, {
      status,
      blockerReason
    });
  }
}