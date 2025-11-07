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

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, CardRepository cardRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    public CommentResponse addComment(UUID cardId, CommentRequest commentRequest) {
        CardsModels card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found with id: " + cardId));
        
        UserModels user = userRepository.findById(UUID.fromString(String.valueOf(commentRequest.getUserId())))
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + commentRequest.getUserId()));

        CommentsModels comment = new CommentsModels();
        comment.setCard(card);
        comment.setUser(user);
        comment.setContent(commentRequest.getContent());

        CommentsModels savedComment = commentRepository.save(comment);

        return toCommentResponse(savedComment);
    }

    private CommentResponse toCommentResponse(CommentsModels comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setUserId(comment.getUser().getId());
        response.setUserName(comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}
