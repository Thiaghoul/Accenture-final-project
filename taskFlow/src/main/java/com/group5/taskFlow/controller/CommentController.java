package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.CommentRequest;
import com.group5.taskFlow.dto.CommentResponse;
import com.group5.taskFlow.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@PathVariable UUID projectId, @PathVariable UUID taskId, @Valid @RequestBody CommentRequest commentRequest) {
        return new ResponseEntity<>(commentService.save(taskId, commentRequest), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getCommentsByTaskId(@PathVariable UUID projectId, @PathVariable UUID taskId) {
        return ResponseEntity.ok(commentService.findByTaskId(taskId));
    }
}
