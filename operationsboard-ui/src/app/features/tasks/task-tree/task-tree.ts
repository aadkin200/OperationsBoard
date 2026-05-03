import { CommonModule } from '@angular/common';
import {
  Component,
  ElementRef,
  HostListener,
  OnInit,
  ViewChild,
  computed,
  signal,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TaskTreeDto, TaskTreeNodeDto } from '../../../core/models/api.models';
import { TaskTreeService } from '../../../core/services/task-tree.service';
import { CreateTaskModal } from '../create-task-modal/create-task-modal';
import { TaskDetail } from '../task-detail/task-detail';

interface TreeCanvasNode {
  id: number;
  title: string;
  status: string;
  priority: string;
  assignee: string;
  dueDate: string | null;
  blockerReason: string | null;
  x: number;
  y: number;
  depth: number;
  hasChildren: boolean;
  collapsed: boolean;
  hiddenDescendantCount: number;
  descendantCount: number;
  completedDescendantCount: number;
  blockedDescendantCount: number;
  overdueDescendantCount: number;
  criticalDescendantCount: number;
  overdue: boolean;
  critical: boolean;
  riskLevel: 'blocked' | 'critical' | 'overdue' | 'normal';
}

interface TreeConnection {
  id: string;
  parentId: number;
  childId: number;
  path: string;
  riskLevel: 'blocked' | 'critical' | 'overdue' | 'normal';
}

@Component({
  selector: 'app-task-tree',
  standalone: true,
  imports: [CommonModule, TaskDetail, CreateTaskModal],
  templateUrl: './task-tree.html',
  styleUrl: './task-tree.scss',
})
export class TaskTree implements OnInit {
  @ViewChild('treeViewport')
  treeViewport?: ElementRef<HTMLDivElement>;

  private readonly nodeWidth = 280;
  private readonly nodeHeight = 212;
  private readonly horizontalGap = 160;
  private readonly verticalGap = 42;

  private isPanning = false;
  private panStartX = 0;
  private panStartY = 0;
  private scrollStartLeft = 0;
  private scrollStartTop = 0;

  tree = signal<TaskTreeDto | null>(null);
  loading = signal(true);
  errorMessage = signal<string | null>(null);
  selectedTaskId = signal<number | null>(null);

  nodes = signal<TreeCanvasNode[]>([]);
  connections = signal<TreeConnection[]>([]);
  collapsedTaskIds = signal<Set<number>>(new Set<number>());

  showCreateChildModal = signal(false);
  parentForNewChild = signal<TreeCanvasNode | null>(null);

  zoom = signal(1);

  canvasWidth = computed(() => {
    const nodes = this.nodes();
    if (nodes.length === 0) return 1200;

    return Math.max(...nodes.map((node) => node.x)) + this.nodeWidth + 240;
  });

  canvasHeight = computed(() => {
    const nodes = this.nodes();
    if (nodes.length === 0) return 700;

    return Math.max(...nodes.map((node) => node.y)) + this.nodeHeight + 180;
  });

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private taskTreeService: TaskTreeService,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const teamId = Number(params.get('teamId'));
      this.loadTree(teamId);
    });
  }

  loadTree(teamId: number): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.taskTreeService.getTeamTaskTree(teamId).subscribe({
      next: (tree) => {
        this.tree.set(tree);
        this.buildTree(tree);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set('Unable to load workflow tree.');
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

  toggleBranch(node: TreeCanvasNode, event: MouseEvent): void {
    event.stopPropagation();

    const next = new Set(this.collapsedTaskIds());

    if (next.has(node.id)) {
      next.delete(node.id);
    } else {
      next.add(node.id);
    }

    this.collapsedTaskIds.set(next);

    const tree = this.tree();
    if (tree) {
      this.buildTree(tree);
    }
  }

  openCreateChildModal(node: TreeCanvasNode, event: MouseEvent): void {
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

  zoomIn(): void {
    this.zoom.set(Math.min(2, Number((this.zoom() + 0.1).toFixed(2))));
  }

  zoomOut(): void {
    this.zoom.set(Math.max(0.5, Number((this.zoom() - 0.1).toFixed(2))));
  }

  resetView(): void {
    this.zoom.set(1);

    const viewport = this.treeViewport?.nativeElement;

    if (viewport) {
      viewport.scrollTo({
        left: 0,
        top: 0,
        behavior: 'smooth',
      });
    }
  }

  fitView(): void {
    const viewport = this.treeViewport?.nativeElement;
    const nodes = this.nodes();

    if (!viewport || nodes.length === 0) {
      return;
    }

    const minX = Math.min(...nodes.map((node) => node.x));
    const minY = Math.min(...nodes.map((node) => node.y));
    const maxX = Math.max(...nodes.map((node) => node.x + this.nodeWidth));
    const maxY = Math.max(...nodes.map((node) => node.y + this.nodeHeight));

    const graphWidth = maxX - minX;
    const graphHeight = maxY - minY;

    const padding = 160;

    const widthScale = (viewport.clientWidth - padding) / graphWidth;
    const heightScale = (viewport.clientHeight - padding) / graphHeight;

    const calculatedZoom = Number(Math.min(widthScale, heightScale).toFixed(2));

    const nextZoom = Math.max(0.25, Math.min(1.5, calculatedZoom));

    this.zoom.set(nextZoom);

    requestAnimationFrame(() => {
      viewport.scrollTo({
        left: Math.max(0, minX * nextZoom - 80),
        top: Math.max(0, minY * nextZoom - 80),
        behavior: 'smooth',
      });
    });
  }

  startPan(event: MouseEvent): void {
    if ((event.target as HTMLElement).closest('.tree-node')) {
      return;
    }

    const viewport = this.treeViewport?.nativeElement;

    if (!viewport) {
      return;
    }

    this.isPanning = true;
    this.panStartX = event.clientX;
    this.panStartY = event.clientY;
    this.scrollStartLeft = viewport.scrollLeft;
    this.scrollStartTop = viewport.scrollTop;

    viewport.classList.add('panning');
  }

  @HostListener('window:mouseup')
  stopPan(): void {
    this.isPanning = false;
    this.treeViewport?.nativeElement.classList.remove('panning');
  }

  @HostListener('window:mousemove', ['$event'])
  handlePan(event: MouseEvent): void {
    if (!this.isPanning) {
      return;
    }

    const viewport = this.treeViewport?.nativeElement;

    if (!viewport) {
      return;
    }

    const deltaX = event.clientX - this.panStartX;
    const deltaY = event.clientY - this.panStartY;

    viewport.scrollLeft = this.scrollStartLeft - deltaX;
    viewport.scrollTop = this.scrollStartTop - deltaY;
  }

  statusClass(status: string): string {
    return status.toLowerCase();
  }

  priorityClass(priority: string): string {
    return priority.toLowerCase();
  }

  completionLabel(node: TreeCanvasNode): string {
    if (node.descendantCount === 0) {
      return 'No dependencies';
    }

    return `${node.completedDescendantCount} / ${node.descendantCount} complete`;
  }

  dependencyStateLabel(node: TreeCanvasNode): string {
    if (node.descendantCount === 0) {
      return 'Standalone';
    }

    if (node.blockedDescendantCount > 0) {
      return `${node.blockedDescendantCount} blocked downstream`;
    }

    if (node.overdueDescendantCount > 0) {
      return `${node.overdueDescendantCount} overdue downstream`;
    }

    if (node.criticalDescendantCount > 0) {
      return `${node.criticalDescendantCount} critical downstream`;
    }

    if (node.completedDescendantCount === node.descendantCount) {
      return 'Dependencies complete';
    }

    return `${node.descendantCount - node.completedDescendantCount} pending`;
  }

  dependencyStateClass(node: TreeCanvasNode): string {
    if (node.descendantCount === 0) {
      return 'none';
    }

    if (node.blockedDescendantCount > 0) {
      return 'blocked';
    }

    if (node.overdueDescendantCount > 0) {
      return 'overdue';
    }

    if (node.criticalDescendantCount > 0) {
      return 'critical';
    }

    if (node.completedDescendantCount === node.descendantCount) {
      return 'complete';
    }

    return 'pending';
  }

  nodeRiskClasses(node: TreeCanvasNode): Record<string, boolean> {
    return {
      'risk-blocked': node.riskLevel === 'blocked',
      'risk-critical': node.riskLevel === 'critical',
      'risk-overdue': node.riskLevel === 'overdue',
      'risk-normal': node.riskLevel === 'normal',
    };
  }

  riskBadgeLabel(node: TreeCanvasNode): string | null {
    if (node.status === 'BLOCKED') {
      return 'Blocked';
    }

    if (node.blockedDescendantCount > 0) {
      return 'Blocked chain';
    }

    if (node.overdue) {
      return 'Overdue';
    }

    if (node.overdueDescendantCount > 0) {
      return 'Overdue chain';
    }

    if (node.critical) {
      return 'Critical';
    }

    if (node.criticalDescendantCount > 0) {
      return 'Critical downstream';
    }

    return null;
  }

  private buildTree(tree: TaskTreeDto): void {
    const nodes: TreeCanvasNode[] = [];
    const connections: TreeConnection[] = [];

    let cursorY = 40;

    for (const root of tree.roots) {
      const subtreeHeight = this.measureVisibleSubtreeHeight(root);
      this.placeNode(root, 0, cursorY, nodes, connections);
      cursorY += subtreeHeight + this.verticalGap + 80;
    }

    this.nodes.set(nodes);
    this.connections.set(connections);
  }

  private placeNode(
    node: TaskTreeNodeDto,
    depth: number,
    y: number,
    nodes: TreeCanvasNode[],
    connections: TreeConnection[],
    parent?: TreeCanvasNode,
  ): TreeCanvasNode {
    const subtreeHeight = this.measureVisibleSubtreeHeight(node);
    const nodeY = y + Math.max(0, (subtreeHeight - this.nodeHeight) / 2);
    const descendants = this.flattenDescendants(node);
    const collapsed = this.collapsedTaskIds().has(node.task.id);
    const overdue = this.isOverdue(node);
    const critical = node.task.priority === 'CRITICAL';

    const blockedDescendantCount = descendants.filter(
      (child) => child.task.status === 'BLOCKED',
    ).length;
    const overdueDescendantCount = descendants.filter((child) => this.isOverdue(child)).length;
    const criticalDescendantCount = descendants.filter(
      (child) => child.task.priority === 'CRITICAL',
    ).length;

    const riskLevel = this.resolveRiskLevel(
      node.task.status,
      critical,
      overdue,
      blockedDescendantCount,
      overdueDescendantCount,
      criticalDescendantCount,
    );

    const treeNode: TreeCanvasNode = {
      id: node.task.id,
      title: node.task.title,
      status: node.task.status,
      priority: node.task.priority,
      assignee: node.task.assignedUser
        ? `${node.task.assignedUser.firstName} ${node.task.assignedUser.lastName}`
        : 'Unassigned',
      dueDate: node.task.dueDate,
      blockerReason: node.task.blockerReason,
      x: 40 + depth * (this.nodeWidth + this.horizontalGap),
      y: nodeY,
      depth,
      hasChildren: node.children.length > 0,
      collapsed,
      hiddenDescendantCount: collapsed ? descendants.length : 0,
      descendantCount: descendants.length,
      completedDescendantCount: descendants.filter((child) => child.task.status === 'COMPLETE')
        .length,
      blockedDescendantCount,
      overdueDescendantCount,
      criticalDescendantCount,
      overdue,
      critical,
      riskLevel,
    };

    nodes.push(treeNode);

    if (parent) {
      connections.push(this.createConnection(parent, treeNode));
    }

    if (collapsed) {
      return treeNode;
    }

    let childY = y;

    for (const child of node.children) {
      const childHeight = this.measureVisibleSubtreeHeight(child);
      this.placeNode(child, depth + 1, childY, nodes, connections, treeNode);
      childY += childHeight + this.verticalGap;
    }

    return treeNode;
  }

  private createConnection(parent: TreeCanvasNode, child: TreeCanvasNode): TreeConnection {
    const startX = parent.x + this.nodeWidth;
    const startY = parent.y + this.nodeHeight / 2;
    const endX = child.x;
    const endY = child.y + this.nodeHeight / 2;
    const midX = startX + Math.max(80, (endX - startX) / 2);

    return {
      id: `${parent.id}-${child.id}`,
      parentId: parent.id,
      childId: child.id,
      riskLevel: this.resolveConnectionRisk(parent, child),
      path: `M ${startX} ${startY} C ${midX} ${startY}, ${midX} ${endY}, ${endX} ${endY}`,
    };
  }

  private resolveConnectionRisk(
    parent: TreeCanvasNode,
    child: TreeCanvasNode,
  ): 'blocked' | 'critical' | 'overdue' | 'normal' {
    if (parent.riskLevel === 'blocked' || child.riskLevel === 'blocked') {
      return 'blocked';
    }

    if (parent.riskLevel === 'overdue' || child.riskLevel === 'overdue') {
      return 'overdue';
    }

    if (parent.riskLevel === 'critical' || child.riskLevel === 'critical') {
      return 'critical';
    }

    return 'normal';
  }

  private resolveRiskLevel(
    status: string,
    critical: boolean,
    overdue: boolean,
    blockedDescendantCount: number,
    overdueDescendantCount: number,
    criticalDescendantCount: number,
  ): 'blocked' | 'critical' | 'overdue' | 'normal' {
    if (status === 'BLOCKED' || blockedDescendantCount > 0) {
      return 'blocked';
    }

    if (overdue || overdueDescendantCount > 0) {
      return 'overdue';
    }

    if (critical || criticalDescendantCount > 0) {
      return 'critical';
    }

    return 'normal';
  }

  private isOverdue(node: TaskTreeNodeDto): boolean {
    if (!node.task.dueDate) {
      return false;
    }

    if (node.task.status === 'COMPLETE' || node.task.status === 'CANCELLED') {
      return false;
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const dueDate = new Date(`${node.task.dueDate}T00:00:00`);
    dueDate.setHours(0, 0, 0, 0);

    return dueDate < today;
  }

  private measureVisibleSubtreeHeight(node: TaskTreeNodeDto): number {
    if (node.children.length === 0) {
      return this.nodeHeight;
    }

    if (this.collapsedTaskIds().has(node.task.id)) {
      return this.nodeHeight + 42;
    }

    const childrenHeight =
      node.children.reduce((total, child) => total + this.measureVisibleSubtreeHeight(child), 0) +
      this.verticalGap * Math.max(0, node.children.length - 1);

    return Math.max(this.nodeHeight, childrenHeight);
  }

  private flattenDescendants(node: TaskTreeNodeDto): TaskTreeNodeDto[] {
    return node.children.flatMap((child) => [child, ...this.flattenDescendants(child)]);
  }
}
