package apsoftware.operationsboard.dto;

import java.util.List;

public class ExecutiveDrilldownDto {

    private String type;
    private String title;
    private String subtitle;
    private List<TaskDto> tasks;

    public ExecutiveDrilldownDto() {
    }

    public ExecutiveDrilldownDto(String type, String title, String subtitle, List<TaskDto> tasks) {
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
        this.tasks = tasks;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<TaskDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDto> tasks) {
        this.tasks = tasks;
    }
}
