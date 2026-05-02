import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  CommentDto,
  MembershipDto,
  TaskAuditDto,
  TaskDto,
  TaskStatus,
} from '../../../core/models/api.models';
import { TaskService } from '../../../core/services/task.service';

@Component({
  selector: 'app-task-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-detail.html',
  styleUrl: './task-detail.scss',
})
export class TaskDetail implements OnChanges {
  @Input() taskId: number | null = null;
  @Input() canManage = false;
  @Input() readOnly = false;

  @Output() close = new EventEmitter<void>();
  @Output() taskChanged = new EventEmitter<void>();

  task = signal<TaskDto | null>(null);
  comments = signal<CommentDto[]>([]);
  audit = signal<TaskAuditDto[]>([]);
  teamMembers = signal<MembershipDto[]>([]);

  loading = signal(false);
  loadingMembers = signal(false);
  errorMessage = signal<string | null>(null);
  savingComment = signal(false);
  savingAssignment = signal(false);
  savingAction = signal(false);

  newComment = '';
  blockerCommentMode = false;
  selectedAssignedUserId = '';
  blockerReason = '';

  constructor(private taskService: TaskService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['taskId'] && this.taskId) {
      this.loadDetail(this.taskId);
    }
  }

  loadDetail(taskId: number): void {
    this.loading.set(true);
    this.errorMessage.set(null);
    this.newComment = '';
    this.blockerCommentMode = false;
    this.blockerReason = '';
    this.teamMembers.set([]);

    this.taskService.getTaskDetailData(taskId).subscribe({
      next: (data) => {
        this.task.set(data.task);
        this.comments.set(data.comments);
        this.audit.set(data.audit);
        this.selectedAssignedUserId = data.task.assignedUser?.id
          ? String(data.task.assignedUser.id)
          : '';

        this.loading.set(false);

        if (this.canManage && !this.readOnly) {
          this.loadTeamMembers(data.task.teamId);
        }
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set('Unable to load task details.');
      },
    });
  }

  loadTeamMembers(teamId: number): void {
    this.loadingMembers.set(true);

    this.taskService.getTeamMembers(teamId).subscribe({
      next: (members) => {
        this.teamMembers.set(members.filter((m) => m.active));
        this.loadingMembers.set(false);
      },
      error: () => {
        this.loadingMembers.set(false);
        this.errorMessage.set('Unable to load team members.');
      },
    });
  }

  closeModal(): void {
    this.close.emit();
  }

  stopClick(event: MouseEvent): void {
    event.stopPropagation();
  }

  submitComment(): void {
    const task = this.task();
    const message = this.newComment.trim();

    if (!task || !message || this.savingComment()) {
      return;
    }

    this.savingComment.set(true);

    const request = this.blockerCommentMode
      ? this.taskService.addBlockerComment(task.id, message)
      : this.taskService.addComment(task.id, message);

    request.subscribe({
      next: () => {
        this.newComment = '';
        this.blockerCommentMode = false;
        this.savingComment.set(false);
        this.reloadCommentsAndAudit(task.id);
      },
      error: () => {
        this.savingComment.set(false);
        this.errorMessage.set('Unable to add comment.');
      },
    });
  }

  saveAssignment(): void {
    const task = this.task();

    if (!task || this.savingAssignment()) {
      return;
    }

    this.savingAssignment.set(true);
    this.errorMessage.set(null);

    const request = this.selectedAssignedUserId
      ? this.taskService.assignTask(task.id, Number(this.selectedAssignedUserId))
      : this.taskService.unclaimTask(task.id);

    request.subscribe({
      next: (updatedTask) => this.handleTaskUpdate(updatedTask),
      error: () => {
        this.savingAssignment.set(false);
        this.errorMessage.set('Unable to update assignment.');
      },
    });
  }

  assignmentChanged(): boolean {
    const task = this.task();
    if (!task) return false;

    const currentAssignedId = task.assignedUser?.id ? String(task.assignedUser.id) : '';

    return currentAssignedId !== this.selectedAssignedUserId;
  }

  claimTask(): void {
    const task = this.task();
    if (!task || this.savingAction()) return;

    this.savingAction.set(true);
    this.errorMessage.set(null);

    this.taskService.claimTask(task.id).subscribe({
      next: (updatedTask) => this.handleTaskUpdate(updatedTask),
      error: () => this.handleActionError('Unable to claim task.'),
    });
  }

  unclaimTask(): void {
    const task = this.task();
    if (!task || this.savingAction()) return;

    this.savingAction.set(true);
    this.errorMessage.set(null);

    this.taskService.unclaimTask(task.id).subscribe({
      next: (updatedTask) => this.handleTaskUpdate(updatedTask),
      error: () => this.handleActionError('Unable to unclaim task.'),
    });
  }

  moveToStatus(status: TaskStatus): void {
    const task = this.task();
    if (!task || this.savingAction()) return;

    if (status === 'BLOCKED' && !this.blockerReason.trim()) {
      this.errorMessage.set('Blocker reason is required.');
      return;
    }

    this.savingAction.set(true);
    this.errorMessage.set(null);

    this.taskService
      .updateStatus(task.id, status, status === 'BLOCKED' ? this.blockerReason.trim() : undefined)
      .subscribe({
        next: (updatedTask) => {
          this.blockerReason = '';
          this.handleTaskUpdate(updatedTask);
        },
        error: (error) => {
          const backendMessage =
            error?.error?.message || error?.error?.error || 'Unable to update task status.';

          this.handleActionError(backendMessage);
        },
      });
  }

  cancelTask(): void {
    const task = this.task();
    if (!task || this.savingAction()) return;

    this.savingAction.set(true);
    this.errorMessage.set(null);

    this.taskService.cancelTask(task.id).subscribe({
      next: (updatedTask) => this.handleTaskUpdate(updatedTask),
      error: () => this.handleActionError('Unable to cancel task.'),
    });
  }

  reopenTask(): void {
    const task = this.task();
    if (!task || this.savingAction()) return;

    this.savingAction.set(true);
    this.errorMessage.set(null);

    this.taskService.reopenTask(task.id).subscribe({
      next: (updatedTask) => this.handleTaskUpdate(updatedTask),
      error: () => this.handleActionError('Unable to reopen task.'),
    });
  }

  private handleTaskUpdate(updatedTask: TaskDto): void {
    this.task.set(updatedTask);
    this.selectedAssignedUserId = updatedTask.assignedUser?.id
      ? String(updatedTask.assignedUser.id)
      : '';

    this.savingAssignment.set(false);
    this.savingAction.set(false);
    this.taskChanged.emit();
    this.reloadCommentsAndAudit(updatedTask.id);
  }

  private handleActionError(message: string): void {
    this.savingAction.set(false);
    this.savingAssignment.set(false);
    this.errorMessage.set(message);
  }

  private reloadCommentsAndAudit(taskId: number): void {
    this.taskService.getComments(taskId).subscribe({
      next: (comments) => this.comments.set(comments),
    });

    this.taskService.getAudit(taskId).subscribe({
      next: (audit) => this.audit.set(audit),
    });
  }

  fullName(user: { firstName: string; lastName: string } | null | undefined): string {
    if (!user) return 'Unknown user';
    return `${user.firstName} ${user.lastName}`;
  }

  auditText(item: TaskAuditDto): string {
    const actor = this.fullName(item.changedBy);

    if (item.action === 'COMMENT_ADDED') {
      return `${actor} added a comment.`;
    }

    if (item.action === 'BLOCKER_COMMENT_ADDED') {
      return `${actor} added a blocker comment.`;
    }

    if (item.fieldName === 'status') {
      return `${actor} changed status from ${item.oldValue || 'None'} to ${item.newValue || 'None'}.`;
    }

    if (item.fieldName === 'assignedUser') {
      return `${actor} changed assignment from ${item.oldValue || 'Unassigned'} to ${item.newValue || 'Unassigned'}.`;
    }

    return `${actor} updated ${item.fieldName || item.action}.`;
  }
}
