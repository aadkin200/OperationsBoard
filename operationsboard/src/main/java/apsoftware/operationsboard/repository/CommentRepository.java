package apsoftware.operationsboard.repository;

import apsoftware.operationsboard.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"task", "user"})
    List<Comment> findByTaskIdOrderByCreatedAtAsc(Long taskId);
}
