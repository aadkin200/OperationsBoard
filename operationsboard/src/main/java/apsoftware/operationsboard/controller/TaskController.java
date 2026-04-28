package apsoftware.operationsboard.controller;

import apsoftware.operationsboard.dto.*;
import apsoftware.operationsboard.entity.Task;
import apsoftware.operationsboard.mapper.DtoMapper;
import apsoftware.operationsboard.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/my")
    public List<TaskDto> getMyTasks(@RequestParam Long currentUserId) {
        return taskService.getVisibleTasks(currentUserId)
                .stream()
                .map(DtoMapper::toTaskDto)
                .toList();
    }

    @GetMapping("/team/{teamId}")
    public List<TaskDto> getTeamTasks(
            @RequestParam Long currentUserId,
            @PathVariable Long teamId
    ) {
        return taskService.getTeamTasks(currentUserId, teamId)
                .stream()
                .map(DtoMapper::toTaskDto)
                .toList();
    }

    @GetMapping("/{taskId}")
    public TaskDto getTaskById(
            @RequestParam Long currentUserId,
            @PathVariable Long taskId
    ) {
        return DtoMapper.toTaskDto(taskService.getTaskById(currentUserId, taskId));
    }

    @PostMapping
    public TaskDto createTask(
            @RequestParam Long currentUserId,
            @RequestBody TaskCreateRequest request
    ) {
        Task task = taskService.createTask(
                currentUserId,
                request.getTeamId(),
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getDueDate()
        );

        return DtoMapper.toTaskDto(task);
    }

    @PatchMapping("/{taskId}/claim")
    public TaskDto claimTask(
            @RequestParam Long currentUserId,
            @PathVariable Long taskId
    ) {
        return DtoMapper.toTaskDto(taskService.claimTask(currentUserId, taskId));
    }

    @PatchMapping("/{taskId}/unclaim")
    public TaskDto unclaimTask(
            @RequestParam Long currentUserId,
            @PathVariable Long taskId
    ) {
        return DtoMapper.toTaskDto(taskService.unclaimTask(currentUserId, taskId));
    }

    @PatchMapping("/{taskId}/assign")
    public TaskDto assignTask(
            @RequestParam Long currentUserId,
            @PathVariable Long taskId,
            @RequestBody TaskAssignRequest request
    ) {
        return DtoMapper.toTaskDto(
                taskService.assignTask(currentUserId, taskId, request.getAssigneeUserId())
        );
    }

    @PatchMapping("/{taskId}/status")
    public TaskDto updateStatus(
            @RequestParam Long currentUserId,
            @PathVariable Long taskId,
            @RequestBody TaskStatusUpdateRequest request
    ) {
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
    public TaskDto cancelTask(
            @RequestParam Long currentUserId,
            @PathVariable Long taskId
    ) {
        return DtoMapper.toTaskDto(taskService.cancelTask(currentUserId, taskId));
    }

    @PatchMapping("/{taskId}/reopen")
    public TaskDto reopenTask(
            @RequestParam Long currentUserId,
            @PathVariable Long taskId
    ) {
        return DtoMapper.toTaskDto(taskService.reopenTask(currentUserId, taskId));
    }
    
    @GetMapping("/team/{teamId}/board")
    public TaskBoardDto getTeamBoard(
            @RequestParam Long currentUserId,
            @PathVariable Long teamId
    ) {
        return taskService.getTeamBoard(currentUserId, teamId);
    }
}
