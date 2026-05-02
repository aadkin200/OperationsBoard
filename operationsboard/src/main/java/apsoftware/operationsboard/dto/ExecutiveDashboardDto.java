package apsoftware.operationsboard.dto;

import java.util.List;

public class ExecutiveDashboardDto {

    private long totalActiveTasks;
    private long totalBlocked;
    private long totalDueSoon;
    private long totalOverdue;
    private long totalCriticalRisk;
    private long totalUnassigned;
    private long completedThisMonth;

    private List<TeamMetricDto> workloadByTeam;
    private List<TeamMetricDto> completedByTeam;
    private List<CompletionTrendDto> completionTrend;
    private List<TeamHealthDto> teamHealth;
    private List<EscalationItemDto> escalationQueue;
    private List<LeadershipActionDto> leadershipActions;
    private List<UserWorkloadDto> workloadByEmployee;
    private List<TeamMetricDto> unassignedBacklogByTeam;

    public ExecutiveDashboardDto() {
    }

    public long getTotalActiveTasks() {
        return totalActiveTasks;
    }

    public void setTotalActiveTasks(long totalActiveTasks) {
        this.totalActiveTasks = totalActiveTasks;
    }

    public long getTotalBlocked() {
        return totalBlocked;
    }

    public void setTotalBlocked(long totalBlocked) {
        this.totalBlocked = totalBlocked;
    }

    public long getTotalDueSoon() {
        return totalDueSoon;
    }

    public void setTotalDueSoon(long totalDueSoon) {
        this.totalDueSoon = totalDueSoon;
    }

    public long getTotalOverdue() {
        return totalOverdue;
    }

    public void setTotalOverdue(long totalOverdue) {
        this.totalOverdue = totalOverdue;
    }

    public long getTotalCriticalRisk() {
        return totalCriticalRisk;
    }

    public void setTotalCriticalRisk(long totalCriticalRisk) {
        this.totalCriticalRisk = totalCriticalRisk;
    }

    public long getTotalUnassigned() {
        return totalUnassigned;
    }

    public void setTotalUnassigned(long totalUnassigned) {
        this.totalUnassigned = totalUnassigned;
    }

    public long getCompletedThisMonth() {
        return completedThisMonth;
    }

    public void setCompletedThisMonth(long completedThisMonth) {
        this.completedThisMonth = completedThisMonth;
    }

    public List<TeamMetricDto> getWorkloadByTeam() {
        return workloadByTeam;
    }

    public void setWorkloadByTeam(List<TeamMetricDto> workloadByTeam) {
        this.workloadByTeam = workloadByTeam;
    }

    public List<TeamMetricDto> getCompletedByTeam() {
        return completedByTeam;
    }

    public void setCompletedByTeam(List<TeamMetricDto> completedByTeam) {
        this.completedByTeam = completedByTeam;
    }

    public List<CompletionTrendDto> getCompletionTrend() {
        return completionTrend;
    }

    public void setCompletionTrend(List<CompletionTrendDto> completionTrend) {
        this.completionTrend = completionTrend;
    }

    public List<TeamHealthDto> getTeamHealth() {
        return teamHealth;
    }

    public void setTeamHealth(List<TeamHealthDto> teamHealth) {
        this.teamHealth = teamHealth;
    }

    public List<EscalationItemDto> getEscalationQueue() {
        return escalationQueue;
    }

    public void setEscalationQueue(List<EscalationItemDto> escalationQueue) {
        this.escalationQueue = escalationQueue;
    }

    public List<LeadershipActionDto> getLeadershipActions() {
        return leadershipActions;
    }

    public void setLeadershipActions(List<LeadershipActionDto> leadershipActions) {
        this.leadershipActions = leadershipActions;
    }

    public List<UserWorkloadDto> getWorkloadByEmployee() {
        return workloadByEmployee;
    }

    public void setWorkloadByEmployee(List<UserWorkloadDto> workloadByEmployee) {
        this.workloadByEmployee = workloadByEmployee;
    }

    public List<TeamMetricDto> getUnassignedBacklogByTeam() {
        return unassignedBacklogByTeam;
    }

    public void setUnassignedBacklogByTeam(List<TeamMetricDto> unassignedBacklogByTeam) {
        this.unassignedBacklogByTeam = unassignedBacklogByTeam;
    }
}