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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserRepository userRepository, CardRepository cardRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    public CommentResponse save(CommentRequest commentRequest) {
        UserModels user = userRepository.findById(commentRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + commentRequest.getUserId()));
        CardsModels card = cardRepository.findById(commentRequest.getCardId())
                .orElseThrow(() -> new EntityNotFoundException("Card not found with id: " + commentRequest.getCardId()));

        CommentsModels comment = new CommentsModels();
        comment.setUser(user);
        comment.setCard(card);
        comment.setContent(commentRequest.getContent());

        CommentsModels savedComment = commentRepository.save(comment);
        return toCommentResponse(savedComment);
    }

    public List<CommentResponse> findByCardId(UUID cardId) {
        return commentRepository.findByCardId(cardId).stream()
                .map(this::toCommentResponse)
                .collect(Collectors.toList());
    }

    public CommentResponse findById(UUID id) {
        return commentRepository.findById(id).map(this::toCommentResponse).orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + id));
    }

    public CommentResponse update(UUID id, CommentRequest commentRequest) {
        CommentsModels existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + id));

        existingComment.setContent(commentRequest.getContent());

        CommentsModels updatedComment = commentRepository.save(existingComment);
        return toCommentResponse(updatedComment);
    }

    public void deleteById(UUID id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }

    private CommentResponse toCommentResponse(CommentsModels commentsModels) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(commentsModels.getId());
        commentResponse.setContent(commentsModels.getContent());
        commentResponse.setCardId(commentsModels.getCard().getId());
        commentResponse.setUserId(commentsModels.getUser().getId());
        commentResponse.setCreatedAt(commentsModels.getCreatedAt());
        return commentResponse;
    }
}