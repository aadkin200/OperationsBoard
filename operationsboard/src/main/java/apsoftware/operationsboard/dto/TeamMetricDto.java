package apsoftware.operationsboard.dto;

public class TeamMetricDto {

    private Long teamId;
    private String teamName;
    private Long count;

    public TeamMetricDto() {
    }

    public TeamMetricDto(Long teamId, String teamName, Long count) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.count = count;
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

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
