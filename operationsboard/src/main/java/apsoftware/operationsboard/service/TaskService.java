package apsoftware.operationsboard.service;

import apsoftware.operationsboard.entity.Task;
import apsoftware.operationsboard.entity.Team;
import apsoftware.operationsboard.entity.User;
import apsoftware.operationsboard.enums.PriorityLevel;
import apsoftware.operationsboard.enums.TaskStatus;
import apsoftware.operationsboard.exception.BadRequestException;
import apsoftware.operationsboard.exception.ForbiddenException;
import apsoftware.operationsboard.exception.ResourceNotFoundException;
import apsoftware.operationsboard.repository.TaskRepository;
import apsoftware.operationsboard.repository.TeamRepository;
import apsoftware.operationsboard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final TaskAuditService taskAuditService;

    public TaskService(
            TaskRepository taskRepository,
            TeamRepository teamRepository,
            UserRepository userRepository,
            PermissionService permissionService,
            TaskAuditService taskAuditService
    ) {
        this.taskRepository = taskRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
        this.taskAuditService = taskAuditService;
    }

    public List<Task> getVisibleTasks(Long currentUserId) {
        User currentUser = getUser(currentUserId);

        if (permissionService.isExecutive(currentUser)) {
            return taskRepository.findByHiddenAfterIsNullOrHiddenAfterAfter(LocalDateTime.now());
        }

        return taskRepository.findByAssignedUserId(currentUserId);
    }

    public List<Task> getTeamTasks(Long currentUserId, Long teamId) {
        User currentUser = getUser(currentUserId);

        if (!permissionService.canViewTeam(currentUser, teamId)) {
            throw new ForbiddenException("You do not have permission to view this team.");
        }

        return taskRepository.findByTeamId(teamId);
    }

    public Task getTaskById(Long currentUserId, Long taskId) {
        User currentUser = getUser(currentUserId);
        Task task = getTask(taskId);

        if (!permissionService.canViewTeam(currentUser, task.getTeam().getId())) {
            throw new ForbiddenException("You do not have permission to view this task.");
        }

        return task;
    }

    @Transactional
    public Task createTask(Long currentUserId, Long teamId, String title, String description, String priorityValue, java.time.LocalDate dueDate) {
        User currentUser = getUser(currentUserId);
        Team team = getTeam(teamId);

        if (!permissionService.isTeamManager(currentUserId, teamId)) {
            throw new ForbiddenException("Only managers can create tasks for this team.");
        }

        if (title == null || title.isBlank()) {
            throw new BadRequestException("Task title is required.");
        }

        Task task = new Task();
        task.setTeam(team);
        task.setTitle(title.trim());
        task.setDescription(description);
        task.setPriority(parsePriority(priorityValue));
        task.setDueDate(dueDate);
        task.setStatus(TaskStatus.OPEN);
        task.setCreatedBy(currentUser);

        Task saved = taskRepository.save(task);

        taskAuditService.record(saved, currentUser, "TASK_CREATED", null, null, saved.getTitle());

        return saved;
    }

    @Transactional
    public Task claimTask(Long currentUserId, Long taskId) {
        User currentUser = getUser(currentUserId);
        Task task = getTask(taskId);

        if (!permissionService.canClaimTask(currentUser, task)) {
            throw new ForbiddenException("You do not have permission to claim this task.");
        }

        if (task.getAssignedUser() != null) {
            throw new BadRequestException("Task is already assigned.");
        }

        if (task.getStatus() != TaskStatus.OPEN) {
            throw new BadRequestException("Only OPEN tasks can be claimed.");
        }

        task.setAssignedUser(currentUser);
        task.setStatus(TaskStatus.CLAIMED);

        Task saved = taskRepository.save(task);

        taskAuditService.record(saved, currentUser, "TASK_CLAIMED", "assigned_user_id", null, currentUser.getId().toString());
        taskAuditService.record(saved, currentUser, "STATUS_CHANGED", "status", TaskStatus.OPEN.name(), TaskStatus.CLAIMED.name());

        return saved;
    }

    @Transactional
    public Task unclaimTask(Long currentUserId, Long taskId) {
        User currentUser = getUser(currentUserId);
        Task task = getTask(taskId);

        boolean isAssignedUser = task.getAssignedUser() != null && task.getAssignedUser().getId().equals(currentUserId);
        boolean isManager = permissionService.canManageTask(currentUser, task);

        if (!isAssignedUser && !isManager) {
            throw new ForbiddenException("Only the assigned user or a manager can unclaim this task.");
        }

        if (task.getAssignedUser() == null) {
            throw new BadRequestException("Task is not currently assigned.");
        }

        String oldAssignedUserId = task.getAssignedUser().getId().toString();
        TaskStatus oldStatus = task.getStatus();

        task.setAssignedUser(null);
        task.setStatus(TaskStatus.OPEN);
        task.setBlockerReason(null);

        Task saved = taskRepository.save(task);

        taskAuditService.record(saved, currentUser, "TASK_UNCLAIMED", "assigned_user_id", oldAssignedUserId, null);
        taskAuditService.record(saved, currentUser, "STATUS_CHANGED", "status", oldStatus.name(), TaskStatus.OPEN.name());

        return saved;
    }

    @Transactional
    public Task assignTask(Long currentUserId, Long taskId, Long assigneeUserId) {
        User currentUser = getUser(currentUserId);
        User assignee = getUser(assigneeUserId);
        Task task = getTask(taskId);

        if (!permissionService.canManageTask(currentUser, task)) {
            throw new ForbiddenException("Only managers can assign tasks.");
        }

        Long teamId = task.getTeam().getId();

        if (!permissionService.isTeamMember(assigneeUserId, teamId)) {
            throw new BadRequestException("Assignee must be a member of the task team.");
        }

        String oldAssignedUserId = task.getAssignedUser() == null ? null : task.getAssignedUser().getId().toString();
        TaskStatus oldStatus = task.getStatus();

        task.setAssignedUser(assignee);

        if (task.getStatus() == TaskStatus.OPEN) {
            task.setStatus(TaskStatus.CLAIMED);
        }

        Task saved = taskRepository.save(task);

        taskAuditService.record(saved, currentUser, "TASK_ASSIGNED", "assigned_user_id", oldAssignedUserId, assigneeUserId.toString());

        if (oldStatus != saved.getStatus()) {
            taskAuditService.record(saved, currentUser, "STATUS_CHANGED", "status", oldStatus.name(), saved.getStatus().name());
        }

        return saved;
    }

    @Transactional
    public Task updateStatus(Long currentUserId, Long taskId, String statusValue, String blockerReason) {
        User currentUser = getUser(currentUserId);
        Task task = getTask(taskId);

        TaskStatus newStatus = parseStatus(statusValue);
        TaskStatus oldStatus = task.getStatus();

        boolean isManager = permissionService.canManageTask(currentUser, task);
        boolean isAssignedUser = permissionService.canUpdateOwnTask(currentUser, task);

        if (!isManager && !isAssignedUser) {
            throw new ForbiddenException("Only the assigned user or a manager can update task status.");
        }

        if (newStatus == TaskStatus.BLOCKED && (blockerReason == null || blockerReason.isBlank())) {
            throw new BadRequestException("Blocker reason is required when blocking a task.");
        }

        if (newStatus == TaskStatus.COMPLETE) {
            task.setCompletedAt(LocalDateTime.now());
            task.setHiddenAfter(LocalDateTime.now().plusDays(7));
        }

        if (newStatus == TaskStatus.BLOCKED) {
            task.setBlockerReason(blockerReason.trim());
        } else {
            task.setBlockerReason(null);
        }

        task.setStatus(newStatus);

        Task saved = taskRepository.save(task);

        taskAuditService.record(saved, currentUser, "STATUS_CHANGED", "status", oldStatus.name(), newStatus.name());

        if (newStatus == TaskStatus.BLOCKED) {
            taskAuditService.record(saved, currentUser, "BLOCKER_ADDED", "blocker_reason", null, task.getBlockerReason());
        }

        return saved;
    }

    @Transactional
    public Task cancelTask(Long currentUserId, Long taskId) {
        User currentUser = getUser(currentUserId);
        Task task = getTask(taskId);

        if (!permissionService.canManageTask(currentUser, task)) {
            throw new ForbiddenException("Only managers can cancel tasks.");
        }

        TaskStatus oldStatus = task.getStatus();

        task.setStatus(TaskStatus.CANCELLED);
        task.setHiddenAfter(LocalDateTime.now().plusDays(7));

        Task saved = taskRepository.save(task);

        taskAuditService.record(saved, currentUser, "TASK_CANCELLED", "status", oldStatus.name(), TaskStatus.CANCELLED.name());

        return saved;
    }

    @Transactional
    public Task reopenTask(Long currentUserId, Long taskId) {
        User currentUser = getUser(currentUserId);
        Task task = getTask(taskId);

        if (!permissionService.canManageTask(currentUser, task)) {
            throw new ForbiddenException("Only managers can reopen tasks.");
        }

        if (task.getStatus() != TaskStatus.COMPLETE && task.getStatus() != TaskStatus.CANCELLED) {
            throw new BadRequestException("Only COMPLETE or CANCELLED tasks can be reopened.");
        }

        TaskStatus oldStatus = task.getStatus();

        task.setStatus(TaskStatus.OPEN);
        task.setAssignedUser(null);
        task.setCompletedAt(null);
        task.setHiddenAfter(null);
        task.setBlockerReason(null);

        Task saved = taskRepository.save(task);

        taskAuditService.record(saved, currentUser, "TASK_REOPENED", "status", oldStatus.name(), TaskStatus.OPEN.name());

        return saved;
    }

    private PriorityLevel parsePriority(String priorityValue) {
        if (priorityValue == null || priorityValue.isBlank()) {
            return PriorityLevel.NORMAL;
        }

        try {
            return PriorityLevel.valueOf(priorityValue.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid priority: " + priorityValue);
        }
    }

    private TaskStatus parseStatus(String statusValue) {
        if (statusValue == null || statusValue.isBlank()) {
            throw new BadRequestException("Status is required.");
        }

        try {
            return TaskStatus.valueOf(statusValue.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid status: " + statusValue);
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private Team getTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));
    }

    private Task getTask(Long taskId) {
        return taskRepository.findByIdWithDetails(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
    }
}
