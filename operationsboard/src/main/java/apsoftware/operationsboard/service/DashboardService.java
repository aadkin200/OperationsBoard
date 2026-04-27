package apsoftware.operationsboard.service;

import apsoftware.operationsboard.dto.DashboardSummaryDto;
import apsoftware.operationsboard.dto.TeamDashboardDto;
import apsoftware.operationsboard.entity.Team;
import apsoftware.operationsboard.entity.User;
import apsoftware.operationsboard.exception.ForbiddenException;
import apsoftware.operationsboard.exception.ResourceNotFoundException;
import apsoftware.operationsboard.repository.TaskRepository;
import apsoftware.operationsboard.repository.TeamRepository;
import apsoftware.operationsboard.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DashboardService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PermissionService permissionService;

    public DashboardService(
            TaskRepository taskRepository,
            UserRepository userRepository,
            TeamRepository teamRepository,
            PermissionService permissionService
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
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
}
