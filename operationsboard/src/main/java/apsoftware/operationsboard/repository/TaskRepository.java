package apsoftware.operationsboard.repository;

import apsoftware.operationsboard.dto.TeamMetricDto;
import apsoftware.operationsboard.dto.UserWorkloadDto;
import apsoftware.operationsboard.entity.Task;
import apsoftware.operationsboard.enums.TaskStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    List<Task> findByTeamId(Long teamId);

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    List<Task> findByAssignedUserId(Long userId);

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    List<Task> findByTeamIdAndStatus(Long teamId, TaskStatus status);

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    List<Task> findByStatus(TaskStatus status);

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    List<Task> findByHiddenAfterIsNullOrHiddenAfterAfter(LocalDateTime now);

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    @Query("select t from Task t where t.id = :id")
    Optional<Task> findByIdWithDetails(@Param("id") Long id);

    @Query("""
            select new apsoftware.operationsboard.dto.TeamMetricDto(
                t.team.id,
                t.team.name,
                count(t)
            )
            from Task t
            where t.status in (
                apsoftware.operationsboard.enums.TaskStatus.OPEN,
                apsoftware.operationsboard.enums.TaskStatus.CLAIMED,
                apsoftware.operationsboard.enums.TaskStatus.IN_PROGRESS,
                apsoftware.operationsboard.enums.TaskStatus.BLOCKED
            )
            group by t.team.id, t.team.name
            order by t.team.name
            """)
    List<TeamMetricDto> countOpenTasksByTeam();

    @Query("""
            select new apsoftware.operationsboard.dto.TeamMetricDto(
                t.team.id,
                t.team.name,
                count(t)
            )
            from Task t
            where t.dueDate < :today
            and t.status not in (
                apsoftware.operationsboard.enums.TaskStatus.COMPLETE,
                apsoftware.operationsboard.enums.TaskStatus.CANCELLED
            )
            group by t.team.id, t.team.name
            order by t.team.name
            """)
    List<TeamMetricDto> countOverdueTasksByTeam(@Param("today") LocalDate today);

    @Query("""
            select new apsoftware.operationsboard.dto.TeamMetricDto(
                t.team.id,
                t.team.name,
                count(t)
            )
            from Task t
            where t.status = apsoftware.operationsboard.enums.TaskStatus.BLOCKED
            group by t.team.id, t.team.name
            order by t.team.name
            """)
    List<TeamMetricDto> countBlockedTasksByTeam();

    @Query("""
            select new apsoftware.operationsboard.dto.TeamMetricDto(
                t.team.id,
                t.team.name,
                count(t)
            )
            from Task t
            where t.status = apsoftware.operationsboard.enums.TaskStatus.COMPLETE
            and t.completedAt >= :startOfMonth
            and t.completedAt < :startOfNextMonth
            group by t.team.id, t.team.name
            order by t.team.name
            """)
    List<TeamMetricDto> countCompletedThisMonthByTeam(
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("startOfNextMonth") LocalDateTime startOfNextMonth
    );

    @Query("""
            select new apsoftware.operationsboard.dto.TeamMetricDto(
                t.team.id,
                t.team.name,
                count(t)
            )
            from Task t
            where t.status in (
                apsoftware.operationsboard.enums.TaskStatus.OPEN,
                apsoftware.operationsboard.enums.TaskStatus.CLAIMED,
                apsoftware.operationsboard.enums.TaskStatus.IN_PROGRESS,
                apsoftware.operationsboard.enums.TaskStatus.BLOCKED
            )
            group by t.team.id, t.team.name
            order by count(t) desc
            """)
    List<TeamMetricDto> countWorkloadByTeam();

    @Query("""
            select new apsoftware.operationsboard.dto.UserWorkloadDto(
                u.id,
                u.username,
                concat(u.firstName, ' ', u.lastName),
                tm.id,
                tm.name,
                count(t)
            )
            from Task t
            join t.assignedUser u
            join t.team tm
            where t.status in (
                apsoftware.operationsboard.enums.TaskStatus.CLAIMED,
                apsoftware.operationsboard.enums.TaskStatus.IN_PROGRESS,
                apsoftware.operationsboard.enums.TaskStatus.BLOCKED
            )
            group by u.id, u.username, u.firstName, u.lastName, tm.id, tm.name
            order by count(t) desc
            """)
    List<UserWorkloadDto> countWorkloadByEmployee();

    @Query("""
            select count(t)
            from Task t
            where t.team.id = :teamId
            and t.status in (
                apsoftware.operationsboard.enums.TaskStatus.OPEN,
                apsoftware.operationsboard.enums.TaskStatus.CLAIMED,
                apsoftware.operationsboard.enums.TaskStatus.IN_PROGRESS,
                apsoftware.operationsboard.enums.TaskStatus.BLOCKED
            )
            """)
    Long countOpenTasksForTeam(@Param("teamId") Long teamId);

    @Query("""
            select count(t)
            from Task t
            where t.team.id = :teamId
            and t.dueDate < :today
            and t.status not in (
                apsoftware.operationsboard.enums.TaskStatus.COMPLETE,
                apsoftware.operationsboard.enums.TaskStatus.CANCELLED
            )
            """)
    Long countOverdueTasksForTeam(@Param("teamId") Long teamId, @Param("today") java.time.LocalDate today);

    @Query("""
            select count(t)
            from Task t
            where t.team.id = :teamId
            and t.status = apsoftware.operationsboard.enums.TaskStatus.BLOCKED
            """)
    Long countBlockedTasksForTeam(@Param("teamId") Long teamId);

    @Query("""
            select count(t)
            from Task t
            where t.team.id = :teamId
            and t.status = apsoftware.operationsboard.enums.TaskStatus.COMPLETE
            and t.completedAt >= :startOfMonth
            and t.completedAt < :startOfNextMonth
            """)
    Long countCompletedThisMonthForTeam(
            @Param("teamId") Long teamId,
            @Param("startOfMonth") java.time.LocalDateTime startOfMonth,
            @Param("startOfNextMonth") java.time.LocalDateTime startOfNextMonth
    );

    @Query("""
            select new apsoftware.operationsboard.dto.UserWorkloadDto(
                u.id,
                u.username,
                concat(u.firstName, ' ', u.lastName),
                tm.id,
                tm.name,
                count(t)
            )
            from Task t
            join t.assignedUser u
            join t.team tm
            where tm.id = :teamId
            and t.status in (
                apsoftware.operationsboard.enums.TaskStatus.CLAIMED,
                apsoftware.operationsboard.enums.TaskStatus.IN_PROGRESS,
                apsoftware.operationsboard.enums.TaskStatus.BLOCKED
            )
            group by u.id, u.username, u.firstName, u.lastName, tm.id, tm.name
            order by count(t) desc
            """)
    List<UserWorkloadDto> countWorkloadByEmployeeForTeam(@Param("teamId") Long teamId);

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    @Query("""
            select t
            from Task t
            where t.assignedUser.id = :userId
            and t.status in :statuses
            order by t.dueDate asc, t.priority desc
            """)
    List<Task> findAssignedTasksForDashboard(
            @Param("userId") Long userId,
            @Param("statuses") Collection<TaskStatus> statuses
    );

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    @Query("""
            select t
            from Task t
            where t.assignedUser.id = :userId
            and t.status = apsoftware.operationsboard.enums.TaskStatus.BLOCKED
            order by t.dueDate asc
            """)
    List<Task> findBlockedTasksForUser(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    @Query("""
            select t
            from Task t
            where t.assignedUser.id = :userId
            and t.dueDate is not null
            and t.dueDate <= :dueBy
            and t.status in :statuses
            order by t.dueDate asc
            """)
    List<Task> findDueSoonTasksForUser(
            @Param("userId") Long userId,
            @Param("dueBy") LocalDate dueBy,
            @Param("statuses") Collection<TaskStatus> statuses
    );

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    @Query("""
            select t
            from Task t
            where t.assignedUser is null
            and t.status = apsoftware.operationsboard.enums.TaskStatus.OPEN
            and t.team.id in :teamIds
            order by t.dueDate asc, t.priority desc
            """)
    List<Task> findClaimableTasksForTeams(@Param("teamIds") Collection<Long> teamIds);

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    @Query("""
            select t
            from Task t
            where t.team.id = :teamId
            and (
                t.hiddenAfter is null
                or t.hiddenAfter > :now
            )
            order by
                case t.priority
                    when apsoftware.operationsboard.enums.PriorityLevel.CRITICAL then 1
                    when apsoftware.operationsboard.enums.PriorityLevel.HIGH then 2
                    when apsoftware.operationsboard.enums.PriorityLevel.NORMAL then 3
                    when apsoftware.operationsboard.enums.PriorityLevel.LOW then 4
                end,
                t.dueDate asc,
                t.createdAt asc
            """)
    List<Task> findBoardTasksByTeamId(
            @Param("teamId") Long teamId,
            @Param("now") LocalDateTime now
    );

    // ── Executive dashboard queries ──────────────────────────────────────────

    @Query("""
            select new apsoftware.operationsboard.dto.TeamMetricDto(
                t.team.id,
                t.team.name,
                count(t)
            )
            from Task t
            where t.status = apsoftware.operationsboard.enums.TaskStatus.OPEN
            and t.assignedUser is null
            group by t.team.id, t.team.name
            order by count(t) desc
            """)
    List<TeamMetricDto> countUnassignedBacklogByTeam();

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    @Query("""
            select t
            from Task t
            where t.status = apsoftware.operationsboard.enums.TaskStatus.BLOCKED
            and t.updatedAt <= :cutoff
            order by t.updatedAt asc
            """)
    List<Task> findLongBlockedTasks(@Param("cutoff") LocalDateTime cutoff);

    @Query("""
            select YEAR(t.completedAt), MONTH(t.completedAt), count(t)
            from Task t
            where t.status = apsoftware.operationsboard.enums.TaskStatus.COMPLETE
            and t.completedAt >= :startDate
            group by YEAR(t.completedAt), MONTH(t.completedAt)
            order by YEAR(t.completedAt) asc, MONTH(t.completedAt) asc
            """)
    List<Object[]> findCompletionTrend(@Param("startDate") LocalDateTime startDate);

    @Query("""
            select count(t)
            from Task t
            where t.dueDate is not null
            and t.dueDate >= :today
            and t.dueDate <= :dueBy
            and t.status not in (
                apsoftware.operationsboard.enums.TaskStatus.COMPLETE,
                apsoftware.operationsboard.enums.TaskStatus.CANCELLED
            )
            """)
    long countTotalDueSoon(
            @Param("today") LocalDate today,
            @Param("dueBy") LocalDate dueBy
    );

    @Query("""
            select count(t)
            from Task t
            where t.priority = apsoftware.operationsboard.enums.PriorityLevel.CRITICAL
            and t.status not in (
                apsoftware.operationsboard.enums.TaskStatus.COMPLETE,
                apsoftware.operationsboard.enums.TaskStatus.CANCELLED
            )
            and (
                t.dueDate < :today
                or t.status = apsoftware.operationsboard.enums.TaskStatus.BLOCKED
            )
            """)
    long countCriticalRisk(@Param("today") LocalDate today);
    
    @Query("""
            select count(t)
            from Task t
            where t.parentTask.id = :parentTaskId
            and t.status not in (
                apsoftware.operationsboard.enums.TaskStatus.COMPLETE,
                apsoftware.operationsboard.enums.TaskStatus.CANCELLED
            )
            """)
    long countIncompleteDirectChildren(@Param("parentTaskId") Long parentTaskId);

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    @Query("""
            select t
            from Task t
            where t.parentTask.id = :parentTaskId
            and t.status not in (
                apsoftware.operationsboard.enums.TaskStatus.COMPLETE,
                apsoftware.operationsboard.enums.TaskStatus.CANCELLED
            )
            order by t.createdAt asc
            """)
    List<Task> findIncompleteDirectChildren(@Param("parentTaskId") Long parentTaskId);
}
