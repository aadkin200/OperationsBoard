package apsoftware.operationsboard.dto;

public class TaskAssignRequest {

    private Long assigneeUserId;

    public TaskAssignRequest() {}

    public Long getAssigneeUserId() {
        return assigneeUserId;
    }

    public void setAssigneeUserId(Long assigneeUserId) {
        this.assigneeUserId = assigneeUserId;
    }
}
