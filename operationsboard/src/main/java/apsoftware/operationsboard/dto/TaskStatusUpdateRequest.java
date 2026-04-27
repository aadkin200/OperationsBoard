package apsoftware.operationsboard.dto;

public class TaskStatusUpdateRequest {

    private String status;
    private String blockerReason;

    public TaskStatusUpdateRequest() {}

    public String getStatus() { return status; }
    public String getBlockerReason() { return blockerReason; }

    public void setStatus(String status) { this.status = status; }
    public void setBlockerReason(String blockerReason) { this.blockerReason = blockerReason; }
}
