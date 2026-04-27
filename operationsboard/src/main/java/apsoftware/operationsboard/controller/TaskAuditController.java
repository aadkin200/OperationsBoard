package apsoftware.operationsboard.controller;

import apsoftware.operationsboard.dto.TaskAuditDto;
import apsoftware.operationsboard.mapper.DtoMapper;
import apsoftware.operationsboard.service.TaskAuditService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/audit")
public class TaskAuditController {

    private final TaskAuditService taskAuditService;

    public TaskAuditController(TaskAuditService taskAuditService) {
        this.taskAuditService = taskAuditService;
    }

    @GetMapping
    public List<TaskAuditDto> getAuditForTask(@PathVariable Long taskId) {
        return taskAuditService.getAuditForTask(taskId)
                .stream()
                .map(DtoMapper::toTaskAuditDto)
                .toList();
    }
}
