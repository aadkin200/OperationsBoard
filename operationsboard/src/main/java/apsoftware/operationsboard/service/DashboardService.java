package apsoftware.operationsboard.service;

import apsoftware.operationsboard.dto.DashboardSummaryDto;
import apsoftware.operationsboard.dto.MemberDashboardDto;
import apsoftware.operationsboard.dto.TaskDto;
import apsoftware.operationsboard.dto.TeamDashboardDto;
import apsoftware.operationsboard.entity.Membership;
import apsoftware.operationsboard.entity.Team;
import apsoftware.operationsboard.entity.User;
import apsoftware.operationsboard.enums.TaskStatus;
import apsoftware.operationsboard.exception.ForbiddenException;
import apsoftware.operationsboard.exception.ResourceNotFoundException;
import apsoftware.operationsboard.mapper.DtoMapper;
import apsoftware.operationsboard.repository.MembershipRepository;
import apsoftware.operationsboard.repository.TaskRepository;
import apsoftware.operationsboard.repository.TeamRepository;
import apsoftware.operationsboard.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final MembershipRepository membershipRepository;
    private final PermissionService permissionService;

    public DashboardService(
            TaskRepository taskRepository,
            UserRepository userRepository,
            TeamRepository teamRepository,
            MembershipRepository membershipRepository,
            PermissionService permissionService
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.membershipRepository = membershipRepository;
        this.permissionService = permissionService;
    }

    public DashboardSummaryDto getExecutiveDashboard(Long currentUserId) {
        User currentUser = getUser(currentUserId);

        if (!permissionService.isExecutive(currentUser)) {
            throw new ForbiddenException("Only executives can view the executive dashboard.");
        }

        return buildDashboardSummary();
    }

    public DashboardSummaryDto getDashboardSummary(Long currentUserId) {
        User currentUser = getUser(currentUserId);

        if (!permissionService.isExecutive(currentUser)) {
            throw new ForbiddenException("Only executives can view this dashboard summary for now.");
        }

        return buildDashboardSummary();
    }

    private DashboardSummaryDto buildDashboardSummary() {
        LocalDate today = LocalDate.now();

        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfNextMonth = today.plusMonths(1).withDayOfMonth(1).atStartOfDay();

        DashboardSummaryDto summary = new DashboardSummaryDto();
        summary.setOpenTasksByTeam(taskRepository.countOpenTasksByTeam());
        summary.setOverdueTasksByTeam(taskRepository.countOverdueTasksByTeam(today));
        summary.setBlockedTasksByTeam(taskRepository.countBlockedTasksByTeam());
        summary.setCompletedThisMonthByTeam(
                taskRepository.countCompletedThisMonthByTeam(startOfMonth, startOfNextMonth)
        );
        summary.setWorkloadByTeam(taskRepository.countWorkloadByTeam());
        summary.setWorkloadByEmployee(taskRepository.countWorkloadByEmployee());

        return summary;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }
    
    public TeamDashboardDto getTeamDashboard(Long currentUserId, Long teamId) {
        User currentUser = getUser(currentUserId);
        Team team = getTeam(teamId);

        boolean canView = permissionService.isExecutive(currentUser)
                || permissionService.isTeamManager(currentUserId, teamId);

        if (!canView) {
            throw new ForbiddenException("Only team managers or executives can view this team dashboard.");
        }

        LocalDate today = LocalDate.now();
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfNextMonth = today.plusMonths(1).withDayOfMonth(1).atStartOfDay();

        TeamDashboardDto dashboard = new TeamDashboardDto();
        dashboard.setTeamId(team.getId());
        dashboard.setTeamName(team.getName());
        dashboard.setOpenTaskCount(taskRepository.countOpenTasksForTeam(teamId));
        dashboard.setOverdueTaskCount(taskRepository.countOverdueTasksForTeam(teamId, today));
        dashboard.setBlockedTaskCount(taskRepository.countBlockedTasksForTeam(teamId));
        dashboard.setCompletedThisMonthCount(
                taskRepository.countCompletedThisMonthForTeam(teamId, startOfMonth, startOfNextMonth)
        );
        dashboard.setWorkloadByEmployee(taskRepository.countWorkloadByEmployeeForTeam(teamId));

        return dashboard;
    }
    
    private Team getTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));
    }
    
    public MemberDashboardDto getMemberDashboard(Long currentUserId) {
        User currentUser = getUser(currentUserId);

        List<TaskStatus> activeStatuses = List.of(
                TaskStatus.CLAIMED,
                TaskStatus.IN_PROGRESS,
                TaskStatus.BLOCKED
        );

        List<TaskDto> myTasks = taskRepository
                .findAssignedTasksForDashboard(currentUserId, activeStatuses)
                .stream()
                .map(DtoMapper::toTaskDto)
                .toList();

        List<TaskDto> blockedTasks = taskRepository
                .findBlockedTasksForUser(currentUserId)
                .stream()
                .map(DtoMapper::toTaskDto)
                .toList();

        List<TaskDto> dueSoonTasks = taskRepository
                .findDueSoonTasksForUser(currentUserId, LocalDate.now().plusDays(7), activeStatuses)
                .stream()
                .map(DtoMapper::toTaskDto)
                .toList();

        List<Long> teamIds = membershipRepository.findByUserId(currentUserId)
                .stream()
                .map(Membership::getTeam)
                .map(team -> team.getId())
                .toList();

        List<TaskDto> claimableTasks = teamIds.isEmpty()
                ? List.of()
                : taskRepository.findClaimableTasksForTeams(teamIds)
                        .stream()
                        .map(DtoMapper::toTaskDto)
                        .toList();

        MemberDashboardDto dashboard = new MemberDashboardDto();
        dashboard.setUserId(currentUser.getId());
        dashboard.setUsername(currentUser.getUsername());
        dashboard.setFullName(currentUser.getFirstName() + " " + currentUser.getLastName());

        dashboard.setMyTasks(myTasks);
        dashboard.setBlockedTasks(blockedTasks);
        dashboard.setDueSoonTasks(dueSoonTasks);
        dashboard.setClaimableTasks(claimableTasks);

        dashboard.setAssignedTaskCount((long) myTasks.size());
        dashboard.setBlockedTaskCount((long) blockedTasks.size());
        dashboard.setDueSoonTaskCount((long) dueSoonTasks.size());
        dashboard.setClaimableTaskCount((long) claimableTasks.size());

        return dashboard;
    }
}
