package apsoftware.operationsboard.dto;

public class TeamHealthDto {

    private long teamId;
    private String teamName;
    private long openCount;
    private long blockedCount;
    private long overdueCount;
    private long unassignedCount;
    private long completedThisMonth;
    private String healthStatus;

    public TeamHealthDto() {
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public long getOpenCount() {
        return openCount;
    }

    public void setOpenCount(long openCount) {
        this.openCount = openCount;
    }

    public long getBlockedCount() {
        return blockedCount;
    }

    public void setBlockedCount(long blockedCount) {
        this.blockedCount = blockedCount;
    }

    public long getOverdueCount() {
        return overdueCount;
    }

    public void setOverdueCount(long overdueCount) {
        this.overdueCount = overdueCount;
    }

    public long getUnassignedCount() {
        return unassignedCount;
    }

    public void setUnassignedCount(long unassignedCount) {
        this.unassignedCount = unassignedCount;
    }

    public long getCompletedThisMonth() {
        return completedThisMonth;
    }

    public void setCompletedThisMonth(long completedThisMonth) {
        this.completedThisMonth = completedThisMonth;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }
}
