package apsoftware.operationsboard.dto;

import java.util.ArrayList;
import java.util.List;

public class TaskTreeNodeDto {

    private TaskDto task;
    private List<TaskTreeNodeDto> children = new ArrayList<>();

    public TaskTreeNodeDto() {
    }

    public TaskTreeNodeDto(TaskDto task) {
        this.task = task;
    }

    public TaskDto getTask() {
        return task;
    }

    public void setTask(TaskDto task) {
        this.task = task;
    }

    public List<TaskTreeNodeDto> getChildren() {
        return children;
    }

    public void setChildren(List<TaskTreeNodeDto> children) {
        this.children = children;
    }
}
