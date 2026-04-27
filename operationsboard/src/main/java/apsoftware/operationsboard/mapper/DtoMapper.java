package apsoftware.operationsboard.mapper;

import apsoftware.operationsboard.dto.*;
import apsoftware.operationsboard.entity.*;

public class DtoMapper {

    private DtoMapper() {
    }

    public static UserDto toUserDto(User user) {
        if (user == null) return null;

        return new UserDto(
                user.getId(),
                user.getEmployeeId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getActive(),
                user.getGlobalAccessLevel()
        );
    }

    public static TeamDto toTeamDto(Team team) {
        if (team == null) return null;

        return new TeamDto(
                team.getId(),
                team.getName(),
                team.getDescription(),
                team.getActive()
        );
    }

    public static MembershipDto toMembershipDto(Membership membership) {
        if (membership == null) return null;

        return new MembershipDto(
                membership.getId(),
                toUserDto(membership.getUser()),
                toTeamDto(membership.getTeam()),
                membership.getRole(),
                membership.getActive()
        );
    }

    public static TaskDto toTaskDto(Task task) {
        if (task == null) return null;

        TaskDto dto = new TaskDto();
        dto.setId(task.getId());

        if (task.getTeam() != null) {
            dto.setTeamId(task.getTeam().getId());
            dto.setTeamName(task.getTeam().getName());
        }

        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        dto.setCreatedBy(toUserDto(task.getCreatedBy()));
        dto.setAssignedUser(toUserDto(task.getAssignedUser()));
        dto.setBlockerReason(task.getBlockerReason());
        dto.setCompletedAt(task.getCompletedAt());
        dto.setHiddenAfter(task.getHiddenAfter());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        return dto;
    }

    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) return null;

        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());

        if (comment.getTask() != null) {
            dto.setTaskId(comment.getTask().getId());
        }

        dto.setUser(toUserDto(comment.getUser()));
        dto.setType(comment.getType());
        dto.setMessage(comment.getMessage());
        dto.setCreatedAt(comment.getCreatedAt());

        return dto;
    }

    public static TaskAuditDto toTaskAuditDto(TaskAudit audit) {
        if (audit == null) return null;

        TaskAuditDto dto = new TaskAuditDto();
        dto.setId(audit.getId());

        if (audit.getTask() != null) {
            dto.setTaskId(audit.getTask().getId());
        }

        dto.setChangedBy(toUserDto(audit.getChangedBy()));
        dto.setAction(audit.getAction());
        dto.setFieldName(audit.getFieldName());
        dto.setOldValue(audit.getOldValue());
        dto.setNewValue(audit.getNewValue());
        dto.setCreatedAt(audit.getCreatedAt());

        return dto;
    }
}
