package apsoftware.operationsboard.dto;

public class TaskBoardDto {

    private Long teamId;
    private String teamName;

    private Boolean canManageBoard;
    private Boolean canCreateTasks;
    private Boolean canClaimTasks;
    private Boolean readOnly;

    private TaskBoardColumnDto open;
    private TaskBoardColumnDto claimed;
    private TaskBoardColumnDto inProgress;
    private TaskBoardColumnDto blocked;
    private TaskBoardColumnDto complete;
    private TaskBoardColumnDto cancelled;

    public TaskBoardDto() {
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

    public Boolean getCanManageBoard() {
        return canManageBoard;
    }

    public void setCanManageBoard(Boolean canManageBoard) {
        this.canManageBoard = canManageBoard;
    }

    public Boolean getCanCreateTasks() {
        return canCreateTasks;
    }

    public void setCanCreateTasks(Boolean canCreateTasks) {
        this.canCreateTasks = canCreateTasks;
    }

    public Boolean getCanClaimTasks() {
        return canClaimTasks;
    }

    public void setCanClaimTasks(Boolean canClaimTasks) {
        this.canClaimTasks = canClaimTasks;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public TaskBoardColumnDto getOpen() {
        return open;
    }

    public void setOpen(TaskBoardColumnDto open) {
        this.open = open;
    }

    public TaskBoardColumnDto getClaimed() {
        return claimed;
    }

    public void setClaimed(TaskBoardColumnDto claimed) {
        this.claimed = claimed;
    }

    public TaskBoardColumnDto getInProgress() {
        return inProgress;
    }

    public void setInProgress(TaskBoardColumnDto inProgress) {
        this.inProgress = inProgress;
    }

    public TaskBoardColumnDto getBlocked() {
        return blocked;
    }

    public void setBlocked(TaskBoardColumnDto blocked) {
        this.blocked = blocked;
    }

    public TaskBoardColumnDto getComplete() {
        return complete;
    }

    public void setComplete(TaskBoardColumnDto complete) {
        this.complete = complete;
    }

    public TaskBoardColumnDto getCancelled() {
        return cancelled;
    }

    public void setCancelled(TaskBoardColumnDto cancelled) {
        this.cancelled = cancelled;
    }
}
