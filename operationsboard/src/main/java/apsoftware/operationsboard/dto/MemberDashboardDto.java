package apsoftware.operationsboard.dto;

import java.util.List;

public class MemberDashboardDto {

    private Long userId;
    private String username;
    private String fullName;

    private Long assignedTaskCount;
    private Long blockedTaskCount;
    private Long dueSoonTaskCount;
    private Long claimableTaskCount;

    private List<TaskDto> myTasks;
    private List<TaskDto> blockedTasks;
    private List<TaskDto> dueSoonTasks;
    private List<TaskDto> claimableTasks;

    public MemberDashboardDto() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getAssignedTaskCount() {
        return assignedTaskCount;
    }

    public void setAssignedTaskCount(Long assignedTaskCount) {
        this.assignedTaskCount = assignedTaskCount;
    }

    public Long getBlockedTaskCount() {
        return blockedTaskCount;
    }

    public void setBlockedTaskCount(Long blockedTaskCount) {
        this.blockedTaskCount = blockedTaskCount;
    }

    public Long getDueSoonTaskCount() {
        return dueSoonTaskCount;
    }

    public void setDueSoonTaskCount(Long dueSoonTaskCount) {
        this.dueSoonTaskCount = dueSoonTaskCount;
    }

    public Long getClaimableTaskCount() {
        return claimableTaskCount;
    }

    public void setClaimableTaskCount(Long claimableTaskCount) {
        this.claimableTaskCount = claimableTaskCount;
    }

    public List<TaskDto> getMyTasks() {
        return myTasks;
    }

    public void setMyTasks(List<TaskDto> myTasks) {
        this.myTasks = myTasks;
    }

    public List<TaskDto> getBlockedTasks() {
        return blockedTasks;
    }

    public void setBlockedTasks(List<TaskDto> blockedTasks) {
        this.blockedTasks = blockedTasks;
    }

    public List<TaskDto> getDueSoonTasks() {
        return dueSoonTasks;
    }

    public void setDueSoonTasks(List<TaskDto> dueSoonTasks) {
        this.dueSoonTasks = dueSoonTasks;
    }

    public List<TaskDto> getClaimableTasks() {
        return claimableTasks;
    }

    public void setClaimableTasks(List<TaskDto> claimableTasks) {
        this.claimableTasks = claimableTasks;
    }
}
