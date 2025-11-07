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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private CommentService commentService;

    private UUID cardId;
    private UUID userId;
    private UUID assigneeId;
    private CardsModels card;
    private UserModels user;
    private UserModels assignee;
    private CommentRequest commentRequest;
    private CommentsModels comment;

    @BeforeEach
    void setUp() {
        cardId = UUID.randomUUID();
        userId = UUID.randomUUID();
        assigneeId = UUID.randomUUID();

        user = new UserModels();
        user.setId(userId);
        user.setEmail("commenter@example.com");

        assignee = new UserModels();
        assignee.setId(assigneeId);
        assignee.setEmail("assignee@example.com");

        card = new CardsModels();
        card.setId(cardId);
        card.setTitle("Test Task");
        card.setAssignee(assignee);

        commentRequest = new CommentRequest();
        commentRequest.setCardId(cardId);
        commentRequest.setUserId(userId);
        commentRequest.setText("This is a test comment.");

        comment = new CommentsModels();
        comment.setId(UUID.randomUUID());
        comment.setText(commentRequest.getText());
        comment.setCard(card);
        comment.setUser(user);
        comment.setCreatedAt(Instant.now());
    }

    @Test
    void save_whenCommentIsCreatedAndAssigneeIsNotCommenter_shouldSendEmail() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(CommentsModels.class))).thenReturn(comment);

        // Act
        CommentResponse result = commentService.save(commentRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(commentRequest.getText());
        verify(emailService, times(1)).sendSimpleMessage(eq(assignee.getEmail()), anyString(), anyString());
    }

    @Test
    void save_whenCommentIsCreatedAndAssigneeIsCommenter_shouldNotSendEmail() {
        // Arrange
        card.setAssignee(user); // Assignee is the same as the commenter
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(CommentsModels.class))).thenReturn(comment);

        // Act
        CommentResponse result = commentService.save(commentRequest);

        // Assert
        assertThat(result).isNotNull();
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    void save_whenCardNotFound_shouldThrowException() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> commentService.save(commentRequest));
        verify(commentRepository, never()).save(any(CommentsModels.class));
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    void save_whenUserNotFound_shouldThrowException() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> commentService.save(commentRequest));
        verify(commentRepository, never()).save(any(CommentsModels.class));
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    void findByCardId_shouldReturnListOfCommentResponses() {
        // Arrange
        when(commentRepository.findByCardId(cardId)).thenReturn(List.of(comment));

        // Act
        List<CommentResponse> results = commentService.findByCardId(cardId);

        // Assert
        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getContent()).isEqualTo(comment.getText());
        verify(commentRepository, times(1)).findByCardId(cardId);
    }
}
