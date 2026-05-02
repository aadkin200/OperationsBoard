import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import {
  CommentDto,
  MembershipDto,
  TaskAssignRequest,
  TaskAuditDto,
  TaskBoardDto,
  TaskCreateRequest,
  TaskDto,
  TaskStatus,
} from '../models/api.models';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private readonly baseUrl = 'http://localhost:8080/api/tasks';
  private readonly teamsUrl = 'http://localhost:8080/api/teams';

  constructor(private http: HttpClient) {}

  getTeamBoard(teamId: number): Observable<TaskBoardDto> {
    return this.http.get<TaskBoardDto>(`${this.baseUrl}/team/${teamId}/board`);
  }

  getTask(taskId: number): Observable<TaskDto> {
    return this.http.get<TaskDto>(`${this.baseUrl}/${taskId}`);
  }

  getComments(taskId: number): Observable<CommentDto[]> {
    return this.http.get<CommentDto[]>(`${this.baseUrl}/${taskId}/comments`);
  }

  getAudit(taskId: number): Observable<TaskAuditDto[]> {
    return this.http.get<TaskAuditDto[]>(`${this.baseUrl}/${taskId}/audit`);
  }

  getTeamMembers(teamId: number): Observable<MembershipDto[]> {
    return this.http.get<MembershipDto[]>(`${this.teamsUrl}/${teamId}/members`);
  }

  getTaskDetailData(taskId: number): Observable<{
    task: TaskDto;
    comments: CommentDto[];
    audit: TaskAuditDto[];
  }> {
    return forkJoin({
      task: this.getTask(taskId),
      comments: this.getComments(taskId),
      audit: this.getAudit(taskId),
    });
  }

  createTask(request: TaskCreateRequest): Observable<TaskDto> {
    return this.http.post<TaskDto>(this.baseUrl, request);
  }

  addComment(taskId: number, message: string): Observable<CommentDto> {
    return this.http.post<CommentDto>(`${this.baseUrl}/${taskId}/comments`, {
      message,
    });
  }

  addBlockerComment(taskId: number, message: string): Observable<CommentDto> {
    return this.http.post<CommentDto>(`${this.baseUrl}/${taskId}/comments/blocker`, {
      message,
    });
  }

  claimTask(taskId: number): Observable<TaskDto> {
    return this.http.patch<TaskDto>(`${this.baseUrl}/${taskId}/claim`, {});
  }

  unclaimTask(taskId: number): Observable<TaskDto> {
    return this.http.patch<TaskDto>(`${this.baseUrl}/${taskId}/unclaim`, {});
  }

  assignTask(taskId: number, assigneeUserId: number): Observable<TaskDto> {
    return this.http.patch<TaskDto>(`${this.baseUrl}/${taskId}/assign`, {
      assigneeUserId,
    });
  }

  updateStatus(taskId: number, status: TaskStatus, blockerReason?: string): Observable<TaskDto> {
    return this.http.patch<TaskDto>(`${this.baseUrl}/${taskId}/status`, {
      status,
      blockerReason,
    });
  }

  cancelTask(taskId: number): Observable<TaskDto> {
    return this.http.patch<TaskDto>(`${this.baseUrl}/${taskId}/cancel`, {});
  }

  reopenTask(taskId: number): Observable<TaskDto> {
    return this.http.patch<TaskDto>(`${this.baseUrl}/${taskId}/reopen`, {});
  }
}
