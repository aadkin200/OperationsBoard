package apsoftware.operationsboard.dto;

import java.util.List;

public class TeamDashboardDto {

    private Long teamId;
    private String teamName;

    private Long openTaskCount;
    private Long overdueTaskCount;
    private Long blockedTaskCount;
    private Long completedThisMonthCount;

    private List<UserWorkloadDto> workloadByEmployee;

    public TeamDashboardDto() {
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Long getOpenTaskCount() {
        return openTaskCount;
    }

    public void setOpenTaskCount(Long openTaskCount) {
        this.openTaskCount = openTaskCount;
    }

    public Long getOverdueTaskCount() {
        return overdueTaskCount;
    }

    public void setOverdueTaskCount(Long overdueTaskCount) {
        this.overdueTaskCount = overdueTaskCount;
    }

    public Long getBlockedTaskCount() {
        return blockedTaskCount;
    }

    public void setBlockedTaskCount(Long blockedTaskCount) {
        this.blockedTaskCount = blockedTaskCount;
    }

    public Long getCompletedThisMonthCount() {
        return completedThisMonthCount;
    }

    public void setCompletedThisMonthCount(Long completedThisMonthCount) {
        this.completedThisMonthCount = completedThisMonthCount;
    }

    public List<UserWorkloadDto> getWorkloadByEmployee() {
        return workloadByEmployee;
    }

    public void setWorkloadByEmployee(List<UserWorkloadDto> workloadByEmployee) {
        this.workloadByEmployee = workloadByEmployee;
    }
}
