import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  MembershipDto,
  PriorityLevel,
  TaskCreateRequest,
  TaskDto,
} from '../../../core/models/api.models';
import { TaskService } from '../../../core/services/task.service';

@Component({
  selector: 'app-create-task-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-task-modal.html',
  styleUrl: './create-task-modal.scss',
})
export class CreateTaskModal implements OnInit {
  @Input({ required: true }) teamId!: number;

  @Output() close = new EventEmitter<void>();
  @Output() created = new EventEmitter<TaskDto>();

  saving = signal(false);
  loadingMembers = signal(false);
  errorMessage = signal<string | null>(null);
  teamMembers = signal<MembershipDto[]>([]);

  title = '';
  description = '';
  priority: PriorityLevel = 'NORMAL';
  dueDate = '';
  selectedAssignedUserId = '';

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    this.loadTeamMembers();
  }

  loadTeamMembers(): void {
    if (!this.teamId) {
      return;
    }

    this.loadingMembers.set(true);

    this.taskService.getTeamMembers(this.teamId).subscribe({
      next: (members) => {
        this.teamMembers.set(members.filter((m) => m.active && m.user.active));
        this.loadingMembers.set(false);
      },
      error: () => {
        this.loadingMembers.set(false);
        this.errorMessage.set('Unable to load team members.');
      },
    });
  }

  closeModal(): void {
    if (!this.saving()) {
      this.close.emit();
    }
  }

  stopClick(event: MouseEvent): void {
    event.stopPropagation();
  }

  submit(): void {
    const title = this.title.trim();

    if (!title || this.saving()) {
      return;
    }

    const request: TaskCreateRequest = {
      teamId: this.teamId,
      title,
      description: this.description.trim(),
      priority: this.priority,
      dueDate: this.dueDate || null,
      assignedUserId: this.selectedAssignedUserId ? Number(this.selectedAssignedUserId) : null,
    };

    this.saving.set(true);
    this.errorMessage.set(null);

    this.taskService.createTask(request).subscribe({
      next: (task) => {
        this.saving.set(false);
        this.created.emit(task);
      },
      error: () => {
        this.saving.set(false);
        this.errorMessage.set('Unable to create task.');
      },
    });
  }
}
