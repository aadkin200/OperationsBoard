package apsoftware.operationsboard.dto;

public class UserWorkloadDto {

    private Long userId;
    private String username;
    private String fullName;
    private Long teamId;
    private String teamName;
    private Long openTaskCount;

    public UserWorkloadDto() {
    }

    public UserWorkloadDto(Long userId, String username, String fullName, Long teamId, String teamName, Long openTaskCount) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.teamId = teamId;
        this.teamName = teamName;
        this.openTaskCount = openTaskCount;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public Long getOpenTaskCount() {
        return openTaskCount;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setOpenTaskCount(Long openTaskCount) {
        this.openTaskCount = openTaskCount;
    }
}
