package apsoftware.operationsboard.controller;

import apsoftware.operationsboard.dto.CommentCreateRequest;
import apsoftware.operationsboard.dto.CommentDto;
import apsoftware.operationsboard.mapper.DtoMapper;
import apsoftware.operationsboard.security.CurrentUserService;
import apsoftware.operationsboard.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final CurrentUserService currentUserService;

    public CommentController(CommentService commentService, CurrentUserService currentUserService) {
        this.commentService = commentService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<CommentDto> getCommentsForTask(@PathVariable Long taskId) {
        Long currentUserId = currentUserService.getCurrentUserId();

        return commentService.getCommentsForTask(currentUserId, taskId)
                .stream()
                .map(DtoMapper::toCommentDto)
                .toList();
    }

    @PostMapping
    public CommentDto addComment(
            @PathVariable Long taskId,
            @RequestBody CommentCreateRequest request
    ) {
        Long currentUserId = currentUserService.getCurrentUserId();

        return DtoMapper.toCommentDto(
                commentService.addComment(currentUserId, taskId, request.getMessage())
        );
    }

    @PostMapping("/blocker")
    public CommentDto addBlockerComment(
            @PathVariable Long taskId,
            @RequestBody CommentCreateRequest request
    ) {
        Long currentUserId = currentUserService.getCurrentUserId();

        return DtoMapper.toCommentDto(
                commentService.addBlockerComment(currentUserId, taskId, request.getMessage())
        );
    }
}
