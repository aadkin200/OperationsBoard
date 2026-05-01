package apsoftware.operationsboard.dto;

import apsoftware.operationsboard.enums.MembershipRole;

public class SidebarTeamDto {

    private Long teamId;
    private String teamName;
    private MembershipRole role;
    private Boolean canViewBoard;
    private Boolean canViewMetrics;
    private Boolean executiveView;

    public SidebarTeamDto() {
    }

    public SidebarTeamDto(Long teamId, String teamName, MembershipRole role, Boolean canViewBoard, Boolean canViewMetrics, Boolean executiveView) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.role = role;
        this.canViewBoard = canViewBoard;
        this.canViewMetrics = canViewMetrics;
        this.executiveView = executiveView;
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

    public MembershipRole getRole() {
        return role;
    }

    public void setRole(MembershipRole role) {
        this.role = role;
    }

    public Boolean getCanViewBoard() {
        return canViewBoard;
    }

    public void setCanViewBoard(Boolean canViewBoard) {
        this.canViewBoard = canViewBoard;
    }

    public Boolean getCanViewMetrics() {
        return canViewMetrics;
    }

    public void setCanViewMetrics(Boolean canViewMetrics) {
        this.canViewMetrics = canViewMetrics;
    }

    public Boolean getExecutiveView() {
        return executiveView;
    }

    public void setExecutiveView(Boolean executiveView) {
        this.executiveView = executiveView;
    }
}
