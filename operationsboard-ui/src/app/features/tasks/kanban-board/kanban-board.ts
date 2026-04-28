import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  CdkDragDrop,
  DragDropModule,
  transferArrayItem
} from '@angular/cdk/drag-drop';

import {
  TaskBoardColumnDto,
  TaskBoardDto,
  TaskDto,
  TaskStatus
} from '../../../core/models/api.models';

import { TaskService } from '../../../core/services/task.service';

@Component({
  selector: 'app-kanban-board',
  imports: [DragDropModule],
  templateUrl: './kanban-board.html',
  styleUrl: './kanban-board.scss'
})
export class KanbanBoard implements OnInit {

  board = signal<TaskBoardDto | null>(null);
  loading = signal(false);
  errorMessage = signal<string | null>(null);

  columns = signal<TaskBoardColumnDto[]>([]);

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    const teamId = Number(this.route.snapshot.paramMap.get('teamId'));
    this.loadBoard(teamId);
  }

  loadBoard(teamId: number): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.taskService.getTeamBoard(teamId).subscribe({
      next: board => {
        this.board.set(board);
        this.columns.set([
          board.open,
          board.claimed,
          board.inProgress,
          board.blocked,
          board.complete,
          board.cancelled
        ]);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set('Unable to load board.');
      }
    });
  }

  connectedDropLists(): string[] {
    return this.columns().map(column => this.dropListId(column.key));
  }

  dropListId(status: string): string {
    return `status-${status}`;
  }

  onTaskDropped(event: CdkDragDrop<TaskDto[]>, targetColumn: TaskBoardColumnDto): void {
    if (!targetColumn.dropEnabled || this.board()?.readOnly) {
      return;
    }

    if (event.previousContainer === event.container) {
      return;
    }

    const task = event.previousContainer.data[event.previousIndex];

    transferArrayItem(
      event.previousContainer.data,
      event.container.data,
      event.previousIndex,
      event.currentIndex
    );

    targetColumn.count = targetColumn.tasks.length;

    this.taskService.updateStatus(task.id, targetColumn.key as TaskStatus).subscribe({
      next: updatedTask => {
        Object.assign(task, updatedTask);
      },
      error: () => {
        this.errorMessage.set('Unable to update task status.');
        const teamId = this.board()?.teamId;
        if (teamId) {
          this.loadBoard(teamId);
        }
      }
    });
  }

  claimTask(task: TaskDto): void {
    this.taskService.claimTask(task.id).subscribe({
      next: () => {
        const teamId = this.board()?.teamId;
        if (teamId) {
          this.loadBoard(teamId);
        }
      },
      error: () => {
        this.errorMessage.set('Unable to claim task.');
      }
    });
  }
}
