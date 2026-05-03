import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TaskTreeDto, TaskTreeNodeDto } from '../../../core/models/api.models';
import { TaskTreeService } from '../../../core/services/task-tree.service';
import { CreateTaskModal } from '../create-task-modal/create-task-modal';
import { TaskDetail } from '../task-detail/task-detail';

@Component({
  selector: 'app-task-tree',
  standalone: true,
  imports: [CommonModule, TaskDetail, CreateTaskModal],
  templateUrl: './task-tree.html',
  styleUrl: './task-tree.scss',
})
export class TaskTree implements OnInit {
  tree = signal<TaskTreeDto | null>(null);
  loading = signal(true);
  errorMessage = signal<string | null>(null);
  selectedTaskId = signal<number | null>(null);
  collapsedTaskIds = signal<Set<number>>(new Set<number>());

  showCreateChildModal = signal(false);
  parentForNewChild = signal<TaskTreeNodeDto | null>(null);

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

  openCreateChildModal(node: TaskTreeNodeDto, event: MouseEvent): void {
    event.stopPropagation();
    this.parentForNewChild.set(node);
    this.showCreateChildModal.set(true);
  }

  closeCreateChildModal(): void {
    this.showCreateChildModal.set(false);
    this.parentForNewChild.set(null);
  }

  childTaskCreated(): void {
    const teamId = this.tree()?.teamId;

    this.showCreateChildModal.set(false);
    this.parentForNewChild.set(null);

    if (teamId) {
      this.loadTree(teamId);
    }
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

  completedDescendantCount(node: TaskTreeNodeDto): number {
    return this.flattenDescendants(node).filter((child) => child.task.status === 'COMPLETE').length;
  }

  totalDescendantCount(node: TaskTreeNodeDto): number {
    return this.flattenDescendants(node).length;
  }

  blockedDescendantCount(node: TaskTreeNodeDto): number {
    return this.flattenDescendants(node).filter((child) => child.task.status === 'BLOCKED').length;
  }

  incompleteDescendantCount(node: TaskTreeNodeDto): number {
    return this.totalDescendantCount(node) - this.completedDescendantCount(node);
  }

  completionPercent(node: TaskTreeNodeDto): number {
    const total = this.totalDescendantCount(node);
    if (total === 0) return 0;

    return Math.round((this.completedDescendantCount(node) / total) * 100);
  }

  progressLabel(node: TaskTreeNodeDto): string {
    return `${this.completedDescendantCount(node)} / ${this.totalDescendantCount(node)} complete`;
  }

  dependencyStateLabel(node: TaskTreeNodeDto): string {
    const total = this.totalDescendantCount(node);
    const blocked = this.blockedDescendantCount(node);
    const incomplete = this.incompleteDescendantCount(node);

    if (total === 0) {
      return 'No dependencies';
    }

    if (blocked > 0) {
      return `${blocked} blocked downstream`;
    }

    if (incomplete === 0) {
      return 'All dependencies complete';
    }

    return `${incomplete} dependencies pending`;
  }

  dependencyStateClass(node: TaskTreeNodeDto): string {
    if (!this.hasChildren(node)) {
      return 'none';
    }

    if (this.blockedDescendantCount(node) > 0) {
      return 'blocked';
    }

    if (this.incompleteDescendantCount(node) === 0) {
      return 'complete';
    }

    return 'pending';
  }

  parentClosureWarning(node: TaskTreeNodeDto): string | null {
    if (!this.hasChildren(node)) {
      return null;
    }

    if (node.task.status === 'COMPLETE') {
      return null;
    }

    const incompleteDirectChildren = node.children.filter(
      (child) => child.task.status !== 'COMPLETE',
    ).length;

    if (incompleteDirectChildren === 0) {
      return null;
    }

    return `${incompleteDirectChildren} direct child ${
      incompleteDirectChildren === 1 ? 'task is' : 'tasks are'
    } still open`;
  }

  private countNode(node: TaskTreeNodeDto): number {
    return 1 + node.children.reduce((total, child) => total + this.countNode(child), 0);
  }

  private flattenDescendants(node: TaskTreeNodeDto): TaskTreeNodeDto[] {
    return node.children.flatMap((child) => [child, ...this.flattenDescendants(child)]);
  }
}
