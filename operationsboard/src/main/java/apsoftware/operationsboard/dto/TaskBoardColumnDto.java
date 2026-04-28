package apsoftware.operationsboard.dto;

import java.util.List;

public class TaskBoardColumnDto {

    private String key;
    private String label;
    private Long count;
    private Boolean dropEnabled;
    private List<TaskDto> tasks;

    public TaskBoardColumnDto() {
    }

    public TaskBoardColumnDto(String key, String label, Boolean dropEnabled, List<TaskDto> tasks) {
        this.key = key;
        this.label = label;
        this.dropEnabled = dropEnabled;
        this.tasks = tasks;
        this.count = tasks == null ? 0L : (long) tasks.size();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
    
    public Boolean getDropEnabled() {
        return dropEnabled;
    }

    public void setDropEnabled(Boolean dropEnabled) {
        this.dropEnabled = dropEnabled;
    }
    
    public List<TaskDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDto> tasks) {
        this.tasks = tasks;
        this.count = tasks == null ? 0L : (long) tasks.size();
    }
}
