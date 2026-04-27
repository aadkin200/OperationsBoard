package apsoftware.operationsboard.controller;

import apsoftware.operationsboard.dto.CommentCreateRequest;
import apsoftware.operationsboard.dto.CommentDto;
import apsoftware.operationsboard.mapper.DtoMapper;
import apsoftware.operationsboard.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentDto> getCommentsForTask(
            @RequestParam Long currentUserId,
            @PathVariable Long taskId
    ) {
        return commentService.getCommentsForTask(currentUserId, taskId)
                .stream()
                .map(DtoMapper::toCommentDto)
                .toList();
    }

    @PostMapping
    public CommentDto addComment(
            @RequestParam Long currentUserId,
            @PathVariable Long taskId,
            @RequestBody CommentCreateRequest request
    ) {
        return DtoMapper.toCommentDto(
                commentService.addComment(currentUserId, taskId, request.getMessage())
        );
    }

    @PostMapping("/blocker")
    public CommentDto addBlockerComment(
            @RequestParam Long currentUserId,
            @PathVariable Long taskId,
            @RequestBody CommentCreateRequest request
    ) {
        return DtoMapper.toCommentDto(
                commentService.addBlockerComment(currentUserId, taskId, request.getMessage())
        );
    }
}
