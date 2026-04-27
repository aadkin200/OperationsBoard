package apsoftware.operationsboard.repository;

import apsoftware.operationsboard.entity.TaskAudit;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskAuditRepository extends JpaRepository<TaskAudit, Long> {
	List<TaskAudit> findByTaskIdOrderByCreatedAtDesc(Long taskId);
}
