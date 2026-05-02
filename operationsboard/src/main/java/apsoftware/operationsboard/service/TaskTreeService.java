package apsoftware.operationsboard.service;

import apsoftware.operationsboard.dto.TaskTreeDto;
import apsoftware.operationsboard.dto.TaskTreeNodeDto;
import apsoftware.operationsboard.entity.Task;
import apsoftware.operationsboard.entity.Team;
import apsoftware.operationsboard.entity.User;
import apsoftware.operationsboard.exception.ForbiddenException;
import apsoftware.operationsboard.exception.ResourceNotFoundException;
import apsoftware.operationsboard.mapper.DtoMapper;
import apsoftware.operationsboard.repository.TaskTreeRepository;
import apsoftware.operationsboard.repository.TeamRepository;
import apsoftware.operationsboard.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TaskTreeService {

    private final TaskTreeRepository taskTreeRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final PermissionService permissionService;

    public TaskTreeService(
            TaskTreeRepository taskTreeRepository,
            TeamRepository teamRepository,
            UserRepository userRepository,
            PermissionService permissionService
    ) {
        this.taskTreeRepository = taskTreeRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
    }

    public TaskTreeDto getTeamTaskTree(Long currentUserId, Long teamId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserId));

        if (!permissionService.canViewTeam(currentUser, teamId)) {
            throw new ForbiddenException("You do not have permission to view this team's task tree.");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));

        List<Task> tasks = taskTreeRepository
                .findByTeamIdAndHiddenAfterIsNullOrHiddenAfterAfterOrderByCreatedAtAsc(teamId, LocalDateTime.now());

        Map<Long, TaskTreeNodeDto> nodeByTaskId = new LinkedHashMap<>();

        for (Task task : tasks) {
            nodeByTaskId.put(task.getId(), new TaskTreeNodeDto(DtoMapper.toTaskDto(task)));
        }

        List<TaskTreeNodeDto> roots = new ArrayList<>();

        for (Task task : tasks) {
            TaskTreeNodeDto node = nodeByTaskId.get(task.getId());

            if (task.getParentTask() == null || !nodeByTaskId.containsKey(task.getParentTask().getId())) {
                roots.add(node);
            } else {
                TaskTreeNodeDto parent = nodeByTaskId.get(task.getParentTask().getId());
                parent.getChildren().add(node);
            }
        }

        return new TaskTreeDto(team.getId(), team.getName(), roots);
    }
}
