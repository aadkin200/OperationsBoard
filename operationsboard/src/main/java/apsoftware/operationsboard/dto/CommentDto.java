package apsoftware.operationsboard.dto;

import apsoftware.operationsboard.enums.CommentType;

import java.time.LocalDateTime;

public class CommentDto {

    private Long id;
    private Long taskId;
    private UserDto user;
    private CommentType type;
    private String message;
    private LocalDateTime createdAt;

    public CommentDto() {}

    public Long getId() { return id; }
    public Long getTaskId() { return taskId; }
    public UserDto getUser() { return user; }
    public CommentType getType() { return type; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public void setUser(UserDto user) { this.user = user; }
    public void setType(CommentType type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
