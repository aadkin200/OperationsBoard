package apsoftware.operationsboard.service;

import apsoftware.operationsboard.dto.TaskBoardColumnDto;
import apsoftware.operationsboard.dto.TaskBoardDto;
import apsoftware.operationsboard.dto.TaskDto;
import apsoftware.operationsboard.entity.Task;
import apsoftware.operationsboard.entity.Team;
import apsoftware.operationsboard.entity.User;
import apsoftware.operationsboard.enums.PriorityLevel;
import apsoftware.operationsboard.enums.TaskStatus;
import apsoftware.operationsboard.exception.BadRequestException;
import apsoftware.operationsboard.exception.ForbiddenException;
import apsoftware.operationsboard.exception.ResourceNotFoundException;
import apsoftware.operationsboard.mapper.DtoMapper;
import apsoftware.operationsboard.repository.TaskRepository;
import apsoftware.operationsboard.repository.TeamRepository;
import apsoftware.operationsboard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public Task createTask(
            Long currentUserId,
            Long teamId,
            Long parentTaskId,
            String title,
            String description,
            String priorityValue,
            java.time.LocalDate dueDate,
            Long assignedUserId
    ) {
        User currentUser = getUser(currentUserId);
        Team team = getTeam(teamId);

        if (!permissionService.isTeamManager(currentUserId, teamId)) {
            throw new ForbiddenException("Only managers can create tasks for this team.");
        }

        if (title == null || title.isBlank()) {
            throw new BadRequestException("Task title is required.");
        }

        Task parentTask = null;

        if (parentTaskId != null) {
            parentTask = getTask(parentTaskId);

            if (!parentTask.getTeam().getId().equals(teamId)) {
                throw new BadRequestException("Child task must belong to the same team as its parent task.");
            }
        }

        User assignedUser = null;

        if (assignedUserId != null) {
            assignedUser = getUser(assignedUserId);

            if (!permissionService.isTeamMember(assignedUserId, teamId)) {
                throw new BadRequestException("Assignee must be a member of the task team.");
            }
        }

        Task task = new Task();
        task.setTeam(team);
        task.setParentTask(parentTask);
        task.setTitle(title.trim());
        task.setDescription(description);
        task.setPriority(parsePriority(priorityValue));
        task.setDueDate(dueDate);
        task.setStatus(assignedUser == null ? TaskStatus.OPEN : TaskStatus.CLAIMED);
        task.setCreatedBy(currentUser);
        task.setAssignedUser(assignedUser);

        Task saved = taskRepository.save(task);

        taskAuditService.record(saved, currentUser, "TASK_CREATED", null, null, saved.getTitle());

        if (parentTask != null) {
            taskAuditService.record(
                    saved,
                    currentUser,
                    "PARENT_LINKED",
                    "parent_task_id",
                    null,
                    parentTask.getId().toString()
            );
        }

        if (assignedUser != null) {
            taskAuditService.record(
                    saved,
                    currentUser,
                    "TASK_ASSIGNED",
                    "assigned_user_id",
                    null,
                    assignedUser.getId().toString()
            );
        }

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

        boolean isOpenUnassignedTask =
                oldStatus == TaskStatus.OPEN && task.getAssignedUser() == null;

        boolean canClaimAndMove =
                isOpenUnassignedTask && permissionService.canClaimTask(currentUser, task);

        if (newStatus == TaskStatus.CANCELLED && !isManager) {
            throw new ForbiddenException("Only managers can cancel tasks.");
        }

        if (newStatus == TaskStatus.COMPLETE && isOpenUnassignedTask && !isManager) {
            throw new BadRequestException("Open tasks must be claimed or started before they can be completed.");
        }

        if (!isManager && !isAssignedUser && !canClaimAndMove) {
            throw new ForbiddenException("Only the assigned user, a manager, or a team member claiming open work can update task status.");
        }

        String oldAssignedUserId = task.getAssignedUser() == null
                ? null
                : task.getAssignedUser().getId().toString();

        if (newStatus == TaskStatus.OPEN) {
            task.setAssignedUser(null);
            task.setCompletedAt(null);
            task.setHiddenAfter(null);
            task.setBlockerReason(null);
        }

        if (canClaimAndMove
                && newStatus != TaskStatus.OPEN
                && newStatus != TaskStatus.CANCELLED) {
            task.setAssignedUser(currentUser);
        }

        if (newStatus == TaskStatus.BLOCKED && (blockerReason == null || blockerReason.isBlank())) {
            throw new BadRequestException("Blocker reason is required when blocking a task.");
        }

        if (newStatus == TaskStatus.COMPLETE) {
            validateParentCanBeCompleted(task);

            task.setCompletedAt(LocalDateTime.now());
            task.setHiddenAfter(LocalDateTime.now().plusDays(7));
        } else if (newStatus != TaskStatus.OPEN) {
            task.setCompletedAt(null);
            task.setHiddenAfter(null);
        }

        if (newStatus == TaskStatus.BLOCKED) {
            task.setBlockerReason(blockerReason.trim());
        } else if (newStatus != TaskStatus.OPEN) {
            task.setBlockerReason(null);
        }

        task.setStatus(newStatus);

        Task saved = taskRepository.save(task);

        taskAuditService.record(saved, currentUser, "STATUS_CHANGED", "status", oldStatus.name(), newStatus.name());

        String newAssignedUserId = saved.getAssignedUser() == null
                ? null
                : saved.getAssignedUser().getId().toString();

        if ((oldAssignedUserId == null && newAssignedUserId != null)
                || (oldAssignedUserId != null && !oldAssignedUserId.equals(newAssignedUserId))) {
            taskAuditService.record(saved, currentUser, "ASSIGNMENT_CHANGED", "assigned_user_id", oldAssignedUserId, newAssignedUserId);
        }

        if (newStatus == TaskStatus.BLOCKED) {
            taskAuditService.record(saved, currentUser, "BLOCKER_ADDED", "blocker_reason", null, saved.getBlockerReason());
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

    public TaskBoardDto getTeamBoard(Long currentUserId, Long teamId) {
        User currentUser = getUser(currentUserId);
        Team team = getTeam(teamId);

        if (!permissionService.canViewTeam(currentUser, teamId)) {
            throw new ForbiddenException("You do not have permission to view this team board.");
        }

        boolean isManager = permissionService.isTeamManager(currentUserId, teamId);
        boolean isExecutive = permissionService.isExecutive(currentUser);
        boolean isMember = permissionService.isTeamMember(currentUserId, teamId);

        boolean canManageBoard = isManager;
        boolean canCreateTasks = isManager;
        boolean canClaimTasks = isMember;
        boolean readOnly = isExecutive && !isMember && !isManager;

        List<Task> tasks = taskRepository.findBoardTasksByTeamId(teamId, LocalDateTime.now());

        List<TaskDto> openTasks = new ArrayList<>();
        List<TaskDto> claimedTasks = new ArrayList<>();
        List<TaskDto> inProgressTasks = new ArrayList<>();
        List<TaskDto> blockedTasks = new ArrayList<>();
        List<TaskDto> completeTasks = new ArrayList<>();
        List<TaskDto> cancelledTasks = new ArrayList<>();

        for (Task task : tasks) {
            TaskDto dto = DtoMapper.toTaskDto(task);

            switch (task.getStatus()) {
                case OPEN -> openTasks.add(dto);
                case CLAIMED -> claimedTasks.add(dto);
                case IN_PROGRESS -> inProgressTasks.add(dto);
                case BLOCKED -> blockedTasks.add(dto);
                case COMPLETE -> completeTasks.add(dto);
                case CANCELLED -> cancelledTasks.add(dto);
            }
        }

        TaskBoardDto board = new TaskBoardDto();
        board.setTeamId(team.getId());
        board.setTeamName(team.getName());

        board.setCanManageBoard(canManageBoard);
        board.setCanCreateTasks(canCreateTasks);
        board.setCanClaimTasks(canClaimTasks);
        board.setReadOnly(readOnly);

        board.setOpen(new TaskBoardColumnDto("OPEN", "Open", canManageBoard || canClaimTasks, openTasks));
        board.setClaimed(new TaskBoardColumnDto("CLAIMED", "Claimed", canManageBoard, claimedTasks));
        board.setInProgress(new TaskBoardColumnDto("IN_PROGRESS", "In Progress", canManageBoard || canClaimTasks, inProgressTasks));
        board.setBlocked(new TaskBoardColumnDto("BLOCKED", "Blocked", canManageBoard || canClaimTasks, blockedTasks));
        board.setComplete(new TaskBoardColumnDto("COMPLETE", "Complete", canManageBoard || canClaimTasks, completeTasks));
        board.setCancelled(new TaskBoardColumnDto("CANCELLED", "Cancelled", canManageBoard, cancelledTasks));

        return board;
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

    private void validateParentCanBeCompleted(Task task) {
        long incompleteChildren = taskRepository.countIncompleteDirectChildren(task.getId());

        if (incompleteChildren == 0) {
            return;
        }

        List<Task> blockingChildren = taskRepository.findIncompleteDirectChildren(task.getId());

        String blockingTitles = blockingChildren.stream()
                .limit(3)
                .map(Task::getTitle)
                .toList()
                .toString();

        throw new BadRequestException(
                "This task cannot be completed because it has "
                        + incompleteChildren
                        + " incomplete child task"
                        + (incompleteChildren == 1 ? "" : "s")
                        + ". Complete or cancel child work first: "
                        + blockingTitles
        );
    }
}