package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.CommentRequest;
import com.group5.taskFlow.dto.CommentResponse;
import com.group5.taskFlow.model.CardsModels;
import com.group5.taskFlow.model.CommentsModels;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.repository.CardRepository;
import com.group5.taskFlow.repository.CommentRepository;
import com.group5.taskFlow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;


    private CommentResponse toCommentResponse(CommentsModels comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getText());
        response.setUserId(comment.getUser().getId());
        response.setUserName(comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
        response.setCreatedAt(comment.getCreatedAt());
        return response;

    }
    @Autowired
    public CommentService(CommentRepository commentRepository, CardRepository cardRepository, UserRepository userRepository, EmailService emailService) {
        this.commentRepository = commentRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public CommentResponse save (CommentRequest commentRequest){
        CardsModels card = cardRepository.findById(commentRequest.getCardId())
                .orElseThrow(() -> new EntityNotFoundException("Card not found with id: " + commentRequest.getCardId()));

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

        return toCommentResponse(savedComment);
    }

    public List<CommentResponse> findByCardId (java.util.UUID cardId){
        return commentRepository.findByCardId(cardId).stream()
                .map(this::toCommentResponse)
                .collect(Collectors.toList());
    }
}