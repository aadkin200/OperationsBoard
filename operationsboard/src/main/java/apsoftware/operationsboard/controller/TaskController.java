package apsoftware.operationsboard.controller;

import apsoftware.operationsboard.dto.*;
import apsoftware.operationsboard.entity.Task;
import apsoftware.operationsboard.mapper.DtoMapper;
import apsoftware.operationsboard.security.CurrentUserService;
import apsoftware.operationsboard.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final CurrentUserService currentUserService;

    public TaskController(TaskService taskService, CurrentUserService currentUserService) {
        this.taskService = taskService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/my")
    public List<TaskDto> getMyTasks() {
        Long currentUserId = currentUserService.getCurrentUserId();
        return taskService.getVisibleTasks(currentUserId)
                .stream()
                .map(DtoMapper::toTaskDto)
                .toList();
    }

    @GetMapping("/team/{teamId}")
    public List<TaskDto> getTeamTasks(@PathVariable Long teamId) {
        Long currentUserId = currentUserService.getCurrentUserId();
        return taskService.getTeamTasks(currentUserId, teamId)
                .stream()
                .map(DtoMapper::toTaskDto)
                .toList();
    }

    @GetMapping("/{taskId}")
    public TaskDto getTaskById(@PathVariable Long taskId) {
        Long currentUserId = currentUserService.getCurrentUserId();
        return DtoMapper.toTaskDto(taskService.getTaskById(currentUserId, taskId));
    }

    @PostMapping
    public TaskDto createTask(@RequestBody TaskCreateRequest request) {
        Long currentUserId = currentUserService.getCurrentUserId();

        Task task = taskService.createTask(
                currentUserId,
                request.getTeamId(),
                request.getParentTaskId(),
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getDueDate(),
                request.getAssignedUserId()
        );

        return DtoMapper.toTaskDto(task);
    }

    @PatchMapping("/{taskId}/claim")
    public TaskDto claimTask(@PathVariable Long taskId) {
        Long currentUserId = currentUserService.getCurrentUserId();
        return DtoMapper.toTaskDto(taskService.claimTask(currentUserId, taskId));
    }

    @PatchMapping("/{taskId}/unclaim")
    public TaskDto unclaimTask(@PathVariable Long taskId) {
        Long currentUserId = currentUserService.getCurrentUserId();
        return DtoMapper.toTaskDto(taskService.unclaimTask(currentUserId, taskId));
    }

    @PatchMapping("/{taskId}/assign")
    public TaskDto assignTask(
            @PathVariable Long taskId,
            @RequestBody TaskAssignRequest request
    ) {
        Long currentUserId = currentUserService.getCurrentUserId();
        return DtoMapper.toTaskDto(
                taskService.assignTask(currentUserId, taskId, request.getAssigneeUserId())
        );
    }

    @PatchMapping("/{taskId}/status")
    public TaskDto updateStatus(
            @PathVariable Long taskId,
            @RequestBody TaskStatusUpdateRequest request
    ) {
        Long currentUserId = currentUserService.getCurrentUserId();
        return DtoMapper.toTaskDto(
                taskService.updateStatus(
                        currentUserId,
                        taskId,
                        request.getStatus(),
                        request.getBlockerReason()
                )
        );
    }

    @PatchMapping("/{taskId}/cancel")
    public TaskDto cancelTask(@PathVariable Long taskId) {
        Long currentUserId = currentUserService.getCurrentUserId();
        return DtoMapper.toTaskDto(taskService.cancelTask(currentUserId, taskId));
    }

    @PatchMapping("/{taskId}/reopen")
    public TaskDto reopenTask(@PathVariable Long taskId) {
        Long currentUserId = currentUserService.getCurrentUserId();
        return DtoMapper.toTaskDto(taskService.reopenTask(currentUserId, taskId));
    }

    @GetMapping("/team/{teamId}/board")
    public TaskBoardDto getTeamBoard(@PathVariable Long teamId) {
        Long currentUserId = currentUserService.getCurrentUserId();
        return taskService.getTeamBoard(currentUserId, teamId);
    }
}