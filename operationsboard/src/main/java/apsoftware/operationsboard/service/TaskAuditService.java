package apsoftware.operationsboard.service;

import apsoftware.operationsboard.entity.Task;
import apsoftware.operationsboard.entity.TaskAudit;
import apsoftware.operationsboard.entity.User;
import apsoftware.operationsboard.repository.TaskAuditRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskAuditService {

    private final TaskAuditRepository taskAuditRepository;

    public TaskAuditService(TaskAuditRepository taskAuditRepository) {
        this.taskAuditRepository = taskAuditRepository;
    }

    public void record(Task task, User changedBy, String action, String fieldName, String oldValue, String newValue) {
        TaskAudit audit = new TaskAudit();
        audit.setTask(task);
        audit.setChangedBy(changedBy);
        audit.setAction(action);
        audit.setFieldName(fieldName);
        audit.setOldValue(oldValue);
        audit.setNewValue(newValue);

        taskAuditRepository.save(audit);
    }

    public List<TaskAudit> getAuditForTask(Long taskId) {
        return taskAuditRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
    }
}