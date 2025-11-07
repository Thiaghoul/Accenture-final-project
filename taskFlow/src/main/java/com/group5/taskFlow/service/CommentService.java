package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.CommentRequest;
import com.group5.taskFlow.dto.CommentResponse;
import com.group5.taskFlow.model.CardsModels;
import com.group5.taskFlow.model.CommentsModels;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.EventType;
import com.group5.taskFlow.repository.CardRepository;
import com.group5.taskFlow.repository.CommentRepository;
import com.group5.taskFlow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ActivityLogService activityLogService;

    @Autowired
    public CommentService(CommentRepository commentRepository, CardRepository cardRepository, UserRepository userRepository, EmailService emailService, ActivityLogService activityLogService) {
        this.commentRepository = commentRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public CommentResponse save(UUID taskId, CommentRequest commentRequest) {
        CardsModels card = cardRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found with id: " + taskId));

        UserModels user = userRepository.findById(commentRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + commentRequest.getUserId()));

        CommentsModels comment = new CommentsModels();
        comment.setText(commentRequest.getText());
        comment.setCard(card);
        comment.setUser(user);
        comment.setCreatedAt(Instant.now());

        CommentsModels savedComment = commentRepository.save(comment);

        // Notify the assignee only if they exist and are not the author of the comment
        if (card.getAssignee() != null && !card.getAssignee().getId().equals(user.getId())) {
            String message = String.format(
                "A new comment was added to the task: \"%s\".\n\nComment: \"%s\"",
                card.getTitle(),
                savedComment.getText()
            );
            emailService.sendSimpleMessage(
                card.getAssignee().getEmail(),
                "New Comment on Task: " + card.getTitle(),
                message
            );
        }

        activityLogService.logActivity(EventType.COMMENT_CREATED, "Comment added to card: " + card.getTitle(), user, card.getColumn().getBoard(), card);
        return toCommentResponse(savedComment);
    }

    public List<CommentResponse> findByTaskId(UUID taskId) {
        return commentRepository.findByCardId(taskId).stream()
                .map(this::toCommentResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse toCommentResponse(CommentsModels comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setText(comment.getText());
        commentResponse.setCreatedAt(comment.getCreatedAt());
        commentResponse.setCardId(comment.getCard().getId());
        commentResponse.setUserId(comment.getUser().getId());
        return commentResponse;
    }
}
