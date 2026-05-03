export type GlobalAccessLevel = 'NONE' | 'EXECUTIVE' | 'SUPER_USER';
export type MembershipRole = 'MEMBER' | 'MANAGER';
export type TaskStatus = 'OPEN' | 'CLAIMED' | 'IN_PROGRESS' | 'BLOCKED' | 'COMPLETE' | 'CANCELLED';
export type PriorityLevel = 'LOW' | 'NORMAL' | 'HIGH' | 'CRITICAL';
export type CommentType = 'GENERAL' | 'BLOCKER' | 'EXECUTIVE';
export type HealthStatus = 'GREEN' | 'YELLOW' | 'RED';

export interface UserDto {
  id: number;
  employeeId: string;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  active: boolean;
  globalAccessLevel: GlobalAccessLevel;
}

export interface TaskDto {
  id: number;
  teamId: number;
  teamName: string;
  title: string;
  description: string;
  status: TaskStatus;
  priority: PriorityLevel;
  dueDate: string | null;
  createdBy: UserDto;
  assignedUser: UserDto | null;
  blockerReason: string | null;
  completedAt: string | null;
  hiddenAfter: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CommentDto {
  id: number;
  taskId: number;
  user: UserDto;
  type: CommentType;
  message: string;
  createdAt: string;
}

export interface TaskAuditDto {
  id: number;
  taskId: number;
  changedBy: UserDto;
  action: string;
  fieldName: string;
  oldValue: string | null;
  newValue: string | null;
  createdAt: string;
}

export interface TaskBoardColumnDto {
  key: TaskStatus;
  label: string;
  count: number;
  dropEnabled: boolean;
  tasks: TaskDto[];
}

export interface TaskBoardDto {
  teamId: number;
  teamName: string;
  canManageBoard: boolean;
  canCreateTasks: boolean;
  canClaimTasks: boolean;
  readOnly: boolean;
  open: TaskBoardColumnDto;
  claimed: TaskBoardColumnDto;
  inProgress: TaskBoardColumnDto;
  blocked: TaskBoardColumnDto;
  complete: TaskBoardColumnDto;
  cancelled: TaskBoardColumnDto;
}

export interface TeamMetricDto {
  teamId: number;
  teamName: string;
  count: number;
}

export interface UserWorkloadDto {
  userId: number;
  username: string;
  fullName: string;
  teamId: number;
  teamName: string;
  openTaskCount: number;
}

export interface TeamHealthDto {
  teamId: number;
  teamName: string;
  openCount: number;
  blockedCount: number;
  overdueCount: number;
  unassignedCount: number;
  completedThisMonth: number;
  healthStatus: HealthStatus;
}

export interface EscalationItemDto {
  taskId: number;
  title: string;
  teamName: string;
  assignedUserName: string;
  priority: PriorityLevel;
  blockerReason: string | null;
  daysBlocked: number;
  dueDate: string | null;
}

export interface LeadershipActionDto {
  severity: string;
  category: string;
  teamName: string;
  message: string;
}

export interface CompletionTrendDto {
  year: number;
  month: number;
  monthLabel: string;
  count: number;
}

export interface ExecutiveDashboardDto {
  totalActiveTasks: number;
  totalBlocked: number;
  totalDueSoon: number;
  totalOverdue: number;
  totalCriticalRisk: number;
  totalUnassigned: number;
  completedThisMonth: number;
  workloadByTeam: TeamMetricDto[];
  completedByTeam: TeamMetricDto[];
  completionTrend: CompletionTrendDto[];
  teamHealth: TeamHealthDto[];
  escalationQueue: EscalationItemDto[];
  leadershipActions: LeadershipActionDto[];
  workloadByEmployee: UserWorkloadDto[];
  unassignedBacklogByTeam: TeamMetricDto[];
}

export interface MemberDashboardDto {
  userId: number;
  username: string;
  fullName: string;
  assignedTaskCount: number;
  blockedTaskCount: number;
  dueSoonTaskCount: number;
  claimableTaskCount: number;
  myTasks: TaskDto[];
  blockedTasks: TaskDto[];
  dueSoonTasks: TaskDto[];
  claimableTasks: TaskDto[];
}

export interface TeamDashboardDto {
  teamId: number;
  teamName: string;
  openTaskCount: number;
  overdueTaskCount: number;
  blockedTaskCount: number;
  completedThisMonthCount: number;
  workloadByEmployee: UserWorkloadDto[];
}

export interface DashboardSummaryDto {
  openTasksByTeam: TeamMetricDto[];
  overdueTasksByTeam: TeamMetricDto[];
  blockedTasksByTeam: TeamMetricDto[];
  completedThisMonthByTeam: TeamMetricDto[];
  workloadByTeam: TeamMetricDto[];
  workloadByEmployee: UserWorkloadDto[];
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface TeamDto {
  id: number;
  name: string;
  description: string | null;
  active: boolean;
}

export interface SidebarTeamDto {
  teamId: number;
  teamName: string;
  role: MembershipRole | null;
  canViewBoard: boolean;
  canViewMetrics: boolean;
  executiveView: boolean;
}

export interface TaskCreateRequest {
  teamId: number;
  title: string;
  description: string;
  priority: PriorityLevel;
  dueDate: string | null;
  assignedUserId: number | null;
}

export interface MembershipDto {
  id: number;
  user: UserDto;
  team: TeamDto;
  role: MembershipRole;
  active: boolean;
  createdAt: string;
}

export interface TaskAssignRequest {
  assigneeUserId: number;
}

export interface ExecutiveDrilldownDto {
  type: string;
  title: string;
  subtitle: string;
  tasks: TaskDto[];
}

export interface TaskTreeNodeDto {
  task: TaskDto;
  children: TaskTreeNodeDto[];
}

export interface TaskTreeDto {
  teamId: number;
  teamName: string;
  roots: TaskTreeNodeDto[];
}

export interface TaskCreateRequest {
  teamId: number;
  parentTaskId: number | null;
  title: string;
  description: string;
  priority: PriorityLevel;
  dueDate: string | null;
  assignedUserId: number | null;
}
