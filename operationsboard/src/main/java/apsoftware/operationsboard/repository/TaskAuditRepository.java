package apsoftware.operationsboard.repository;

import apsoftware.operationsboard.entity.TaskAudit;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskAuditRepository extends JpaRepository<TaskAudit, Long> {

    @EntityGraph(attributePaths = {"task", "changedBy"})
    List<TaskAudit> findByTaskIdOrderByCreatedAtDesc(Long taskId);
}
