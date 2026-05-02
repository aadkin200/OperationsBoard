import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TaskTreeDto, TaskTreeNodeDto } from '../../../core/models/api.models';
import { TaskTreeService } from '../../../core/services/task-tree.service';
import { TaskDetail } from '../task-detail/task-detail';

@Component({
  selector: 'app-task-tree',
  standalone: true,
  imports: [CommonModule, TaskDetail],
  templateUrl: './task-tree.html',
  styleUrl: './task-tree.scss',
})
export class TaskTree implements OnInit {
  tree = signal<TaskTreeDto | null>(null);
  loading = signal(true);
  errorMessage = signal<string | null>(null);
  selectedTaskId = signal<number | null>(null);
  collapsedTaskIds = signal<Set<number>>(new Set<number>());

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private taskTreeService: TaskTreeService,
  ) {}

  ngOnInit(): void {
    const teamId = Number(this.route.snapshot.paramMap.get('teamId'));
    this.loadTree(teamId);
  }

  loadTree(teamId: number): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.taskTreeService.getTeamTaskTree(teamId).subscribe({
      next: (tree) => {
        this.tree.set(tree);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Unable to load task tree.');
        this.loading.set(false);
      },
    });
  }

  goToBoard(): void {
    const teamId = this.tree()?.teamId;
    if (teamId) {
      this.router.navigate(['/team', teamId, 'board']);
    }
  }

  openTask(taskId: number): void {
    this.selectedTaskId.set(taskId);
  }

  closeTask(): void {
    this.selectedTaskId.set(null);
  }

  toggleNode(node: TaskTreeNodeDto, event: MouseEvent): void {
    event.stopPropagation();

    const next = new Set(this.collapsedTaskIds());

    if (next.has(node.task.id)) {
      next.delete(node.task.id);
    } else {
      next.add(node.task.id);
    }

    this.collapsedTaskIds.set(next);
  }

  isCollapsed(node: TaskTreeNodeDto): boolean {
    return this.collapsedTaskIds().has(node.task.id);
  }

  hasChildren(node: TaskTreeNodeDto): boolean {
    return node.children.length > 0;
  }

  statusClass(status: string): string {
    return status.toLowerCase();
  }

  priorityClass(priority: string): string {
    return priority.toLowerCase();
  }

  assigneeLabel(node: TaskTreeNodeDto): string {
    return node.task.assignedUser
      ? `${node.task.assignedUser.firstName} ${node.task.assignedUser.lastName}`
      : 'Unassigned';
  }

  totalNodes(): number {
    const tree = this.tree();
    if (!tree) return 0;

    return tree.roots.reduce((total, node) => total + this.countNode(node), 0);
  }

  private countNode(node: TaskTreeNodeDto): number {
    return 1 + node.children.reduce((total, child) => total + this.countNode(child), 0);
  }
}
