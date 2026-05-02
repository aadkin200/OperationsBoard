package apsoftware.operationsboard.dto;

public class LeadershipActionDto {

    private String severity;
    private String category;
    private String teamName;
    private String message;

    public LeadershipActionDto() {
    }

    public LeadershipActionDto(String severity, String category, String teamName, String message) {
        this.severity = severity;
        this.category = category;
        this.teamName = teamName;
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}