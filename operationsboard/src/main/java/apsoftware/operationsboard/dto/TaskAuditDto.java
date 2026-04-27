package apsoftware.operationsboard.dto;

import java.time.LocalDateTime;

public class TaskAuditDto {

    private Long id;
    private Long taskId;
    private UserDto changedBy;
    private String action;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private LocalDateTime createdAt;

    public TaskAuditDto() {}

    public Long getId() { return id; }
    public Long getTaskId() { return taskId; }
    public UserDto getChangedBy() { return changedBy; }
    public String getAction() { return action; }
    public String getFieldName() { return fieldName; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public void setChangedBy(UserDto changedBy) { this.changedBy = changedBy; }
    public void setAction(String action) { this.action = action; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
