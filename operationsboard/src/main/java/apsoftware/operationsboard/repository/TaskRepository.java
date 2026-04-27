package apsoftware.operationsboard.repository;

import apsoftware.operationsboard.entity.Task;
import apsoftware.operationsboard.enums.TaskStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    List<Task> findByTeamId(Long teamId);

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    List<Task> findByAssignedUserId(Long userId);

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    List<Task> findByTeamIdAndStatus(Long teamId, TaskStatus status);

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    List<Task> findByStatus(TaskStatus status);

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    List<Task> findByHiddenAfterIsNullOrHiddenAfterAfter(LocalDateTime now);

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser"})
    @Query("select t from Task t where t.id = :id")
    Optional<Task> findByIdWithDetails(@Param("id") Long id);
}
