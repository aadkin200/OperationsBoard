package apsoftware.operationsboard.service;

import apsoftware.operationsboard.entity.Comment;
import apsoftware.operationsboard.entity.Task;
import apsoftware.operationsboard.entity.User;
import apsoftware.operationsboard.enums.CommentType;
import apsoftware.operationsboard.exception.BadRequestException;
import apsoftware.operationsboard.exception.ForbiddenException;
import apsoftware.operationsboard.exception.ResourceNotFoundException;
import apsoftware.operationsboard.repository.CommentRepository;
import apsoftware.operationsboard.repository.TaskRepository;
import apsoftware.operationsboard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final TaskAuditService taskAuditService;

    public CommentService(
            CommentRepository commentRepository,
            TaskRepository taskRepository,
            UserRepository userRepository,
            PermissionService permissionService,
            TaskAuditService taskAuditService
    ) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
        this.taskAuditService = taskAuditService;
    }

    public List<Comment> getCommentsForTask(Long currentUserId, Long taskId) {
        User currentUser = getUser(currentUserId);
        Task task = getTask(taskId);

        if (!permissionService.canViewTeam(currentUser, task.getTeam().getId())) {
            throw new ForbiddenException("You do not have permission to view comments for this task.");
        }

        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
    }

    @Transactional
    public Comment addComment(Long currentUserId, Long taskId, String message) {
        User currentUser = getUser(currentUserId);
        Task task = getTask(taskId);

        if (!permissionService.canCommentOnTask(currentUser, task)) {
            throw new ForbiddenException("You do not have permission to comment on this task.");
        }

        if (message == null || message.isBlank()) {
            throw new BadRequestException("Comment message is required.");
        }

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setUser(currentUser);
        comment.setMessage(message.trim());

        if (permissionService.isExecutive(currentUser)
                && !permissionService.isTeamMember(currentUserId, task.getTeam().getId())) {
            comment.setType(CommentType.EXECUTIVE);
        } else {
            comment.setType(CommentType.GENERAL);
        }

        Comment saved = commentRepository.save(comment);

        taskAuditService.record(task, currentUser, "COMMENT_ADDED", "comment", null, saved.getMessage());

        return saved;
    }

    @Transactional
    public Comment addBlockerComment(Long currentUserId, Long taskId, String message) {
        User currentUser = getUser(currentUserId);
        Task task = getTask(taskId);

        if (!permissionService.canCommentOnTask(currentUser, task)) {
            throw new ForbiddenException("You do not have permission to comment on this task.");
        }

        if (message == null || message.isBlank()) {
            throw new BadRequestException("Blocker comment is required.");
        }

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setUser(currentUser);
        comment.setType(CommentType.BLOCKER);
        comment.setMessage(message.trim());

        Comment saved = commentRepository.save(comment);

        taskAuditService.record(task, currentUser, "BLOCKER_COMMENT_ADDED", "comment", null, saved.getMessage());

        return saved;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private Task getTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
    }
}
