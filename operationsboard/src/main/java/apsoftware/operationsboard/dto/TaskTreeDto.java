package apsoftware.operationsboard.dto;

import java.util.ArrayList;
import java.util.List;

public class TaskTreeDto {

    private Long teamId;
    private String teamName;
    private List<TaskTreeNodeDto> roots = new ArrayList<>();

    public TaskTreeDto() {
    }

    public TaskTreeDto(Long teamId, String teamName, List<TaskTreeNodeDto> roots) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.roots = roots;
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

    public List<TaskTreeNodeDto> getRoots() {
        return roots;
    }

    public void setRoots(List<TaskTreeNodeDto> roots) {
        this.roots = roots;
    }
}
