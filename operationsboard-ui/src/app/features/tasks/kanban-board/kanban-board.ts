import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CdkDragDrop, DragDropModule, transferArrayItem } from '@angular/cdk/drag-drop';
import { BlockerModal } from '../blocker-modal/blocker-modal';
import { TaskDetail } from '../task-detail/task-detail';
import { CreateTaskModal } from '../create-task-modal/create-task-modal';

import {
  TaskBoardColumnDto,
  TaskBoardDto,
  TaskDto,
  TaskStatus,
} from '../../../core/models/api.models';

import { TaskService } from '../../../core/services/task.service';

@Component({
  selector: 'app-kanban-board',
  standalone: true,
  imports: [DragDropModule, BlockerModal, TaskDetail, CreateTaskModal],
  templateUrl: './kanban-board.html',
  styleUrl: './kanban-board.scss',
})
export class KanbanBoard implements OnInit {
  board = signal<TaskBoardDto | null>(null);
  loading = signal(false);
  errorMessage = signal<string | null>(null);
  showCreateTaskModal = signal(false);

  columns = signal<TaskBoardColumnDto[]>([]);

  showBlockerModal = signal(false);
  pendingBlockedTask = signal<TaskDto | null>(null);
  pendingBlockedColumn = signal<TaskBoardColumnDto | null>(null);

  selectedTaskId = signal<number | null>(null);

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
  ) {}

  ngOnInit(): void {
    const teamId = Number(this.route.snapshot.paramMap.get('teamId'));
    this.loadBoard(teamId);
  }

  loadBoard(teamId: number, clearError = true): void {
    this.loading.set(true);

    if (clearError) {
      this.errorMessage.set(null);
    }

    this.taskService.getTeamBoard(teamId).subscribe({
      next: (board) => {
        this.board.set(board);
        this.columns.set([
          board.open,
          board.claimed,
          board.inProgress,
          board.blocked,
          board.complete,
          board.cancelled,
        ]);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set('Unable to load board.');
      },
    });
  }

  connectedDropLists(): string[] {
    return this.columns().map((column) => this.dropListId(column.key));
  }

  dropListId(status: string): string {
    return `status-${status}`;
  }

  openTaskDetail(task: TaskDto, event?: MouseEvent): void {
    if (event) {
      event.stopPropagation();
    }

    this.selectedTaskId.set(task.id);
  }

  closeTaskDetail(): void {
    this.selectedTaskId.set(null);
  }

  refreshBoardAfterTaskChange(): void {
    const teamId = this.board()?.teamId;
    if (teamId) {
      this.loadBoard(teamId);
    }
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
      event.currentIndex,
    );

    targetColumn.count = targetColumn.tasks.length;

    if (targetColumn.key === 'BLOCKED') {
      this.pendingBlockedTask.set(task);
      this.pendingBlockedColumn.set(targetColumn);
      this.showBlockerModal.set(true);
      return;
    }

    this.taskService.updateStatus(task.id, targetColumn.key as TaskStatus).subscribe({
      next: (updatedTask) => {
        const targetTasks = targetColumn.tasks;
        const index = targetTasks.findIndex((t) => t.id === updatedTask.id);

        if (index !== -1) {
          targetTasks[index] = { ...updatedTask };
        }

        targetColumn.count = targetColumn.tasks.length;
        this.columns.set([...this.columns()]);
      },
      error: (error) => {
        this.errorMessage.set(this.getBackendErrorMessage(error, 'Unable to update task status.'));

        const teamId = this.board()?.teamId;
        if (teamId) {
          this.loadBoard(teamId, false);
        }
      },
    });
  }

  claimTask(task: TaskDto, event?: MouseEvent): void {
    if (event) {
      event.stopPropagation();
    }

    this.taskService.claimTask(task.id).subscribe({
      next: () => {
        const teamId = this.board()?.teamId;
        if (teamId) {
          this.loadBoard(teamId);
        }
      },
      error: (error) => {
        this.errorMessage.set(this.getBackendErrorMessage(error, 'Unable to claim task.'));
      },
    });
  }

  cancelBlockerModal(): void {
    this.showBlockerModal.set(false);
    this.pendingBlockedTask.set(null);
    this.pendingBlockedColumn.set(null);

    const teamId = this.board()?.teamId;
    if (teamId) {
      this.loadBoard(teamId, false);
    }
  }

  submitBlockerReason(reason: string): void {
    const task = this.pendingBlockedTask();
    const targetColumn = this.pendingBlockedColumn();

    if (!task || !targetColumn) {
      return;
    }

    this.taskService.updateStatus(task.id, 'BLOCKED', reason).subscribe({
      next: (updatedTask) => {
        const targetTasks = targetColumn.tasks;
        const index = targetTasks.findIndex((t) => t.id === updatedTask.id);

        if (index !== -1) {
          targetTasks[index] = { ...updatedTask };
        }

        targetColumn.count = targetColumn.tasks.length;
        this.columns.set([...this.columns()]);

        this.showBlockerModal.set(false);
        this.pendingBlockedTask.set(null);
        this.pendingBlockedColumn.set(null);
      },
      error: (error) => {
        this.errorMessage.set(this.getBackendErrorMessage(error, 'Unable to block task.'));
        this.cancelBlockerModal();
      },
    });
  }

  openCreateTaskModal(): void {
    this.showCreateTaskModal.set(true);
  }

  closeCreateTaskModal(): void {
    this.showCreateTaskModal.set(false);
  }

  taskCreated(): void {
    this.showCreateTaskModal.set(false);

    const teamId = this.board()?.teamId;
    if (teamId) {
      this.loadBoard(teamId);
    }
  }

  private getBackendErrorMessage(error: any, fallback: string): string {
    if (typeof error?.error === 'string') {
      return error.error;
    }

    return error?.error?.message || error?.error?.error || error?.message || fallback;
  }

  dismissError(): void {
    this.errorMessage.set(null);
  }
}
