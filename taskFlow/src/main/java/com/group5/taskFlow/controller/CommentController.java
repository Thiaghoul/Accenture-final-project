package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.CommentRequest;
import com.group5.taskFlow.dto.CommentResponse;
import com.group5.taskFlow.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment (@Valid @RequestBody CommentRequest commentRequest){
        return ResponseEntity.ok(commentService.save(commentRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable UUID id) {
        return ResponseEntity.ok(commentService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable UUID id, @Valid @RequestBody CommentRequest commentRequest) {
        return ResponseEntity.ok(commentService.update(id, commentRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID id) {
        commentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByCardId (@PathVariable UUID cardId){
        return ResponseEntity.ok(commentService.findByCardId(cardId));
    }

}
