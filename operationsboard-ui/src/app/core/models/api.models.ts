export type GlobalAccessLevel = 'NONE' | 'EXECUTIVE' | 'SUPER_USER';
export type MembershipRole = 'MEMBER' | 'MANAGER';
export type TaskStatus = 'OPEN' | 'CLAIMED' | 'IN_PROGRESS' | 'BLOCKED' | 'COMPLETE' | 'CANCELLED';
export type PriorityLevel = 'LOW' | 'NORMAL' | 'HIGH' | 'CRITICAL';
export type CommentType = 'GENERAL' | 'BLOCKER' | 'EXECUTIVE';

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