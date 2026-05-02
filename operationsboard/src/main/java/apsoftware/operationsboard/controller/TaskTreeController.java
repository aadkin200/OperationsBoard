package apsoftware.operationsboard.controller;

import apsoftware.operationsboard.dto.TaskTreeDto;
import apsoftware.operationsboard.security.CurrentUserService;
import apsoftware.operationsboard.service.TaskTreeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskTreeController {

    private final TaskTreeService taskTreeService;
    private final CurrentUserService currentUserService;

    public TaskTreeController(TaskTreeService taskTreeService, CurrentUserService currentUserService) {
        this.taskTreeService = taskTreeService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/team/{teamId}/tree")
    public TaskTreeDto getTeamTaskTree(@PathVariable Long teamId) {
        Long currentUserId = currentUserService.getCurrentUserId();
        return taskTreeService.getTeamTaskTree(currentUserId, teamId);
    }
}