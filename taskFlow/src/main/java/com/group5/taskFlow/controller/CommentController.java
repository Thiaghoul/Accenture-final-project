package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.CommentRequest;
import com.group5.taskFlow.dto.CommentResponse;
import com.group5.taskFlow.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cards/{cardId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@PathVariable UUID cardId, @RequestBody CommentRequest commentRequest) {
        CommentResponse newComment = commentService.addComment(cardId, commentRequest);
        return new ResponseEntity<>(newComment, HttpStatus.CREATED);
    }
}
