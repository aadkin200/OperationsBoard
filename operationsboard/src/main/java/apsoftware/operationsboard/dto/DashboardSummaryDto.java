package apsoftware.operationsboard.dto;

import java.util.List;

public class DashboardSummaryDto {

    private List<TeamMetricDto> openTasksByTeam;
    private List<TeamMetricDto> overdueTasksByTeam;
    private List<TeamMetricDto> blockedTasksByTeam;
    private List<TeamMetricDto> completedThisMonthByTeam;
    private List<TeamMetricDto> workloadByTeam;
    private List<UserWorkloadDto> workloadByEmployee;

    public DashboardSummaryDto() {
    }

    public List<TeamMetricDto> getOpenTasksByTeam() {
        return openTasksByTeam;
    }

    public void setOpenTasksByTeam(List<TeamMetricDto> openTasksByTeam) {
        this.openTasksByTeam = openTasksByTeam;
    }

    public List<TeamMetricDto> getOverdueTasksByTeam() {
        return overdueTasksByTeam;
    }

    public void setOverdueTasksByTeam(List<TeamMetricDto> overdueTasksByTeam) {
        this.overdueTasksByTeam = overdueTasksByTeam;
    }

    public List<TeamMetricDto> getBlockedTasksByTeam() {
        return blockedTasksByTeam;
    }

    public void setBlockedTasksByTeam(List<TeamMetricDto> blockedTasksByTeam) {
        this.blockedTasksByTeam = blockedTasksByTeam;
    }

    public List<TeamMetricDto> getCompletedThisMonthByTeam() {
        return completedThisMonthByTeam;
    }

    public void setCompletedThisMonthByTeam(List<TeamMetricDto> completedThisMonthByTeam) {
        this.completedThisMonthByTeam = completedThisMonthByTeam;
    }

    public List<TeamMetricDto> getWorkloadByTeam() {
        return workloadByTeam;
    }

    public void setWorkloadByTeam(List<TeamMetricDto> workloadByTeam) {
        this.workloadByTeam = workloadByTeam;
    }

    public List<UserWorkloadDto> getWorkloadByEmployee() {
        return workloadByEmployee;
    }

    public void setWorkloadByEmployee(List<UserWorkloadDto> workloadByEmployee) {
        this.workloadByEmployee = workloadByEmployee;
    }
}
