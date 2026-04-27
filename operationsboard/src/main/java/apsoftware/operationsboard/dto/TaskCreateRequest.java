package apsoftware.operationsboard.dto;

import java.time.LocalDate;

public class TaskCreateRequest {

    private Long teamId;
    private String title;
    private String description;
    private String priority;
    private LocalDate dueDate;

    public TaskCreateRequest() {}

    public Long getTeamId() { return teamId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }
    public LocalDate getDueDate() { return dueDate; }

    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
