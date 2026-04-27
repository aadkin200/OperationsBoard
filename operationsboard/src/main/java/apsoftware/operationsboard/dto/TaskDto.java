package apsoftware.operationsboard.dto;

import apsoftware.operationsboard.enums.PriorityLevel;
import apsoftware.operationsboard.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskDto {

    private Long id;
    private Long teamId;
    private String teamName;
    private String title;
    private String description;
    private TaskStatus status;
    private PriorityLevel priority;
    private LocalDate dueDate;
    private UserDto createdBy;
    private UserDto assignedUser;
    private String blockerReason;
    private LocalDateTime completedAt;
    private LocalDateTime hiddenAfter;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TaskDto() {}

    public Long getId() { return id; }
    public Long getTeamId() { return teamId; }
    public String getTeamName() { return teamName; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public PriorityLevel getPriority() { return priority; }
    public LocalDate getDueDate() { return dueDate; }
    public UserDto getCreatedBy() { return createdBy; }
    public UserDto getAssignedUser() { return assignedUser; }
    public String getBlockerReason() { return blockerReason; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public LocalDateTime getHiddenAfter() { return hiddenAfter; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public void setPriority(PriorityLevel priority) { this.priority = priority; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setCreatedBy(UserDto createdBy) { this.createdBy = createdBy; }
    public void setAssignedUser(UserDto assignedUser) { this.assignedUser = assignedUser; }
    public void setBlockerReason(String blockerReason) { this.blockerReason = blockerReason; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public void setHiddenAfter(LocalDateTime hiddenAfter) { this.hiddenAfter = hiddenAfter; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
