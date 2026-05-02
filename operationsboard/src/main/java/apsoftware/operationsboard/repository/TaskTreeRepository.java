package apsoftware.operationsboard.repository;

import apsoftware.operationsboard.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskTreeRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {"team", "createdBy", "assignedUser", "parentTask"})
    List<Task> findByTeamIdAndHiddenAfterIsNullOrHiddenAfterAfterOrderByCreatedAtAsc(
            Long teamId,
            LocalDateTime now
    );
}
