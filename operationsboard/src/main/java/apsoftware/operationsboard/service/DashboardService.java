package apsoftware.operationsboard.service;

import apsoftware.operationsboard.dto.CompletionTrendDto;
import apsoftware.operationsboard.dto.DashboardSummaryDto;
import apsoftware.operationsboard.dto.EscalationItemDto;
import apsoftware.operationsboard.dto.ExecutiveDashboardDto;
import apsoftware.operationsboard.dto.MemberDashboardDto;
import apsoftware.operationsboard.dto.TaskDto;
import apsoftware.operationsboard.dto.TeamDashboardDto;
import apsoftware.operationsboard.dto.TeamHealthDto;
import apsoftware.operationsboard.dto.TeamMetricDto;
import apsoftware.operationsboard.entity.Membership;
import apsoftware.operationsboard.entity.Task;
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
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    public ExecutiveDashboardDto getExecutiveDashboard(Long currentUserId) {
        User currentUser = getUser(currentUserId);

        if (!permissionService.isExecutive(currentUser)) {
            throw new ForbiddenException("Only executives can view the executive dashboard.");
        }

        return buildExecutiveDashboard();
    }

    public DashboardSummaryDto getDashboardSummary(Long currentUserId) {
        User currentUser = getUser(currentUserId);

        if (!permissionService.isExecutive(currentUser)) {
            throw new ForbiddenException("Only executives can view this dashboard summary.");
        }

        return buildDashboardSummary();
    }

    private ExecutiveDashboardDto buildExecutiveDashboard() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfNextMonth = today.plusMonths(1).withDayOfMonth(1).atStartOfDay();
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime trendStart = LocalDateTime.now().minusMonths(3).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        List<TeamMetricDto> workloadByTeam = taskRepository.countWorkloadByTeam();
        List<TeamMetricDto> blockedByTeam = taskRepository.countBlockedTasksByTeam();
        List<TeamMetricDto> overdueByTeam = taskRepository.countOverdueTasksByTeam(today);
        List<TeamMetricDto> completedByTeam = taskRepository.countCompletedThisMonthByTeam(startOfMonth, startOfNextMonth);
        List<TeamMetricDto> unassignedByTeam = taskRepository.countUnassignedBacklogByTeam();

        long totalActive = workloadByTeam.stream().mapToLong(TeamMetricDto::getCount).sum();
        long totalBlocked = blockedByTeam.stream().mapToLong(TeamMetricDto::getCount).sum();
        long totalOverdue = overdueByTeam.stream().mapToLong(TeamMetricDto::getCount).sum();
        long totalCompleted = completedByTeam.stream().mapToLong(TeamMetricDto::getCount).sum();
        long totalUnassigned = unassignedByTeam.stream().mapToLong(TeamMetricDto::getCount).sum();
        long totalDueSoon = taskRepository.countTotalDueSoon(today, today.plusDays(7));
        long totalCriticalRisk = taskRepository.countCriticalRisk(today);

        List<EscalationItemDto> escalationQueue = buildEscalationQueue(sevenDaysAgo);
        List<CompletionTrendDto> completionTrend = buildCompletionTrend(trendStart, today);
        List<TeamHealthDto> teamHealth = buildTeamHealth(workloadByTeam, blockedByTeam, overdueByTeam, unassignedByTeam, completedByTeam);

        ExecutiveDashboardDto dto = new ExecutiveDashboardDto();
        dto.setTotalActiveTasks(totalActive);
        dto.setTotalBlocked(totalBlocked);
        dto.setTotalOverdue(totalOverdue);
        dto.setTotalDueSoon(totalDueSoon);
        dto.setTotalCriticalRisk(totalCriticalRisk);
        dto.setTotalUnassigned(totalUnassigned);
        dto.setCompletedThisMonth(totalCompleted);
        dto.setWorkloadByTeam(workloadByTeam);
        dto.setCompletedByTeam(completedByTeam);
        dto.setCompletionTrend(completionTrend);
        dto.setTeamHealth(teamHealth);
        dto.setEscalationQueue(escalationQueue);
        dto.setWorkloadByEmployee(taskRepository.countWorkloadByEmployee());
        dto.setUnassignedBacklogByTeam(unassignedByTeam);

        return dto;
    }

    private List<EscalationItemDto> buildEscalationQueue(LocalDateTime cutoff) {
        List<Task> longBlocked = taskRepository.findLongBlockedTasks(cutoff);
        List<EscalationItemDto> items = new ArrayList<>();

        for (Task task : longBlocked) {
            EscalationItemDto item = new EscalationItemDto();
            item.setTaskId(task.getId());
            item.setTitle(task.getTitle());
            item.setTeamName(task.getTeam().getName());
            item.setPriority(task.getPriority().name());
            item.setBlockerReason(task.getBlockerReason());
            item.setDaysBlocked(java.time.temporal.ChronoUnit.DAYS.between(task.getUpdatedAt(), LocalDateTime.now()));

            if (task.getAssignedUser() != null) {
                item.setAssignedUserName(task.getAssignedUser().getFirstName() + " " + task.getAssignedUser().getLastName());
            } else {
                item.setAssignedUserName("Unassigned");
            }

            if (task.getDueDate() != null) {
                item.setDueDate(task.getDueDate().toString());
            }

            items.add(item);
        }

        return items;
    }

    private List<CompletionTrendDto> buildCompletionTrend(LocalDateTime startDate, LocalDate today) {
        List<Object[]> raw = taskRepository.findCompletionTrend(startDate);
        Map<String, Long> countsByYearMonth = new HashMap<>();

        for (Object[] row : raw) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            long count = ((Number) row[2]).longValue();
            countsByYearMonth.put(year + "-" + month, count);
        }

        List<CompletionTrendDto> trend = new ArrayList<>();
        LocalDate cursor = startDate.toLocalDate().withDayOfMonth(1);
        LocalDate endMonth = today.withDayOfMonth(1);

        while (!cursor.isAfter(endMonth)) {
            int year = cursor.getYear();
            int month = cursor.getMonthValue();
            String label = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.US) + " " + year;
            long count = countsByYearMonth.getOrDefault(year + "-" + month, 0L);
            trend.add(new CompletionTrendDto(year, month, label, count));
            cursor = cursor.plusMonths(1);
        }

        return trend;
    }

    private List<TeamHealthDto> buildTeamHealth(
            List<TeamMetricDto> workload,
            List<TeamMetricDto> blocked,
            List<TeamMetricDto> overdue,
            List<TeamMetricDto> unassigned,
            List<TeamMetricDto> completed
    ) {
        Map<Long, Long> blockedMap = toMap(blocked);
        Map<Long, Long> overdueMap = toMap(overdue);
        Map<Long, Long> unassignedMap = toMap(unassigned);
        Map<Long, Long> completedMap = toMap(completed);

        List<TeamHealthDto> result = new ArrayList<>();

        for (TeamMetricDto team : workload) {
            long teamId = team.getTeamId();
            long blockedCount = blockedMap.getOrDefault(teamId, 0L);
            long overdueCount = overdueMap.getOrDefault(teamId, 0L);
            long unassignedCount = unassignedMap.getOrDefault(teamId, 0L);
            long completedCount = completedMap.getOrDefault(teamId, 0L);

            String health;
            if (overdueCount > 0 || blockedCount >= 2) {
                health = "RED";
            } else if (blockedCount >= 1 || unassignedCount >= 3) {
                health = "YELLOW";
            } else {
                health = "GREEN";
            }

            TeamHealthDto dto = new TeamHealthDto();
            dto.setTeamId(teamId);
            dto.setTeamName(team.getTeamName());
            dto.setOpenCount(team.getCount());
            dto.setBlockedCount(blockedCount);
            dto.setOverdueCount(overdueCount);
            dto.setUnassignedCount(unassignedCount);
            dto.setCompletedThisMonth(completedCount);
            dto.setHealthStatus(health);

            result.add(dto);
        }

        return result;
    }

    private Map<Long, Long> toMap(List<TeamMetricDto> list) {
        Map<Long, Long> map = new HashMap<>();
        for (TeamMetricDto dto : list) {
            map.put(dto.getTeamId(), dto.getCount());
        }
        return map;
    }

    private DashboardSummaryDto buildDashboardSummary() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfNextMonth = today.plusMonths(1).withDayOfMonth(1).atStartOfDay();

        DashboardSummaryDto summary = new DashboardSummaryDto();
        summary.setOpenTasksByTeam(taskRepository.countOpenTasksByTeam());
        summary.setOverdueTasksByTeam(taskRepository.countOverdueTasksByTeam(today));
        summary.setBlockedTasksByTeam(taskRepository.countBlockedTasksByTeam());
        summary.setCompletedThisMonthByTeam(taskRepository.countCompletedThisMonthByTeam(startOfMonth, startOfNextMonth));
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
        dashboard.setCompletedThisMonthCount(taskRepository.countCompletedThisMonthForTeam(teamId, startOfMonth, startOfNextMonth));
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
