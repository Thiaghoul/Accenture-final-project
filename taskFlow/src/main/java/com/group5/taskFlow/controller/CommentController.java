package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.CommentRequest;
import com.group5.taskFlow.dto.CommentResponse;
import com.group5.taskFlow.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentRequest commentRequest) {
        return ResponseEntity.ok(commentService.save(commentRequest));
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByCardId(@PathVariable UUID cardId) {
        return ResponseEntity.ok(commentService.findByCardId(cardId));
    }
}
