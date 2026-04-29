import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CdkDragDrop, DragDropModule, transferArrayItem } from '@angular/cdk/drag-drop';
import { BlockerModal } from '../blocker-modal/blocker-modal';

import {
  TaskBoardColumnDto,
  TaskBoardDto,
  TaskDto,
  TaskStatus,
} from '../../../core/models/api.models';

import { TaskService } from '../../../core/services/task.service';

@Component({
  selector: 'app-kanban-board',
  imports: [DragDropModule, BlockerModal],
  templateUrl: './kanban-board.html',
  styleUrl: './kanban-board.scss',
})
export class KanbanBoard implements OnInit {
  board = signal<TaskBoardDto | null>(null);
  loading = signal(false);
  errorMessage = signal<string | null>(null);

  columns = signal<TaskBoardColumnDto[]>([]);

  showBlockerModal = signal(false);
  pendingBlockedTask = signal<TaskDto | null>(null);
  pendingBlockedColumn = signal<TaskBoardColumnDto | null>(null);

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
  ) {}

  ngOnInit(): void {
    const teamId = Number(this.route.snapshot.paramMap.get('teamId'));
    this.loadBoard(teamId);
  }

  loadBoard(teamId: number): void {
    this.loading.set(true);
    this.errorMessage.set(null);

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
      error: () => {
        this.errorMessage.set('Unable to update task status.');
        const teamId = this.board()?.teamId;
        if (teamId) {
          this.loadBoard(teamId);
        }
      },
    });
  }

  // onTaskDropped(event: CdkDragDrop<TaskDto[]>, targetColumn: TaskBoardColumnDto): void {
  //   if (!targetColumn.dropEnabled || this.board()?.readOnly) {
  //     return;
  //   }

  //   if (event.previousContainer === event.container) {
  //     return;
  //   }

  //   const task = event.previousContainer.data[event.previousIndex];

  //   transferArrayItem(
  //     event.previousContainer.data,
  //     event.container.data,
  //     event.previousIndex,
  //     event.currentIndex,
  //   );

  //   targetColumn.count = targetColumn.tasks.length;

  //   let blockerReason: string | undefined;

  //   if (targetColumn.key === 'BLOCKED') {
  //     const reason = prompt('Why is this task blocked?');

  //     if (!reason || reason.trim().length === 0) {
  //       this.loadBoard(this.board()!.teamId);
  //       return;
  //     }

  //     blockerReason = reason.trim();
  //   }

  //   if (targetColumn.key === 'BLOCKED') {
  //     this.pendingBlockedTask.set(task);
  //     this.pendingBlockedColumn.set(targetColumn);
  //     this.showBlockerModal.set(true);
  //     return;
  //   }

  //   this.taskService
  //     .updateStatus(task.id, targetColumn.key as TaskStatus, blockerReason)
  //     .subscribe({
  //       next: (updatedTask) => {
  //         const targetTasks = targetColumn.tasks;
  //         const index = targetTasks.findIndex((t) => t.id === updatedTask.id);

  //         if (index !== -1) {
  //           targetTasks[index] = { ...updatedTask };
  //         }

  //         this.columns.set([...this.columns()]);
  //       },
  //       error: () => {
  //         this.errorMessage.set('Unable to update task status.');
  //         const teamId = this.board()?.teamId;
  //         if (teamId) {
  //           this.loadBoard(teamId);
  //         }
  //       },
  //     });
  // }

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
      },
    });
  }

  cancelBlockerModal(): void {
    this.showBlockerModal.set(false);
    this.pendingBlockedTask.set(null);
    this.pendingBlockedColumn.set(null);

    const teamId = this.board()?.teamId;
    if (teamId) {
      this.loadBoard(teamId);
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
      error: () => {
        this.errorMessage.set('Unable to block task.');
        this.cancelBlockerModal();
      },
    });
  }
}
