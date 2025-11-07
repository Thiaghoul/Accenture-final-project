package com.group5.taskFlow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group5.taskFlow.dto.CommentRequest;
import com.group5.taskFlow.model.CardsModels;
import com.group5.taskFlow.model.CommentsModels;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.repository.CardRepository;
import com.group5.taskFlow.repository.CommentRepository;
import com.group5.taskFlow.repository.UserRepository;
import com.group5.taskFlow.service.EmailService;
import com.group5.taskFlow.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EmailService emailService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UUID cardId;
    private UUID userId;
    private UUID assigneeId;
    private CardsModels card;
    private UserModels user;
    private UserModels assignee;
    private CommentsModels comment;
    private String validJwtToken;

    @BeforeEach
    void setUp() {
        cardId = UUID.randomUUID();
        userId = UUID.randomUUID();
        assigneeId = UUID.randomUUID();

        user = new UserModels();
        user.setId(userId);
        user.setEmail("commenter@example.com");
        user.setPasswordHash("mock-password-hash"); 

        assignee = new UserModels();
        assignee.setId(assigneeId);
        assignee.setEmail("assignee@example.com");

        card = new CardsModels();
        card.setId(cardId);
        card.setTitle("Test Task");
        card.setAssignee(assignee);

        comment = new CommentsModels();
        comment.setId(UUID.randomUUID());
        comment.setContent("Test Comment");
        comment.setCard(card);
        comment.setUser(user);
        comment.setCreatedAt(Instant.now());

        validJwtToken = "mock-jwt-token";
        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromJWT(anyString())).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user)); // Crucial for UserDetailsService
    }

    @Test
    void createComment_whenValidRequestAndAssigneeIsNotCommenter_shouldReturnOkAndSendEmail() throws Exception {
        // Arrange
        CommentRequest request = new CommentRequest();
        request.setCardId(cardId);
        request.setUserId(userId);
        request.setContent("New comment text");

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(CommentsModels.class))).thenReturn(comment);

        // Act & Assert
        mockMvc.perform(post("/api/v1/comments")
                        .header("Authorization", "Bearer " + validJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("This is a test comment."));

        verify(commentRepository, times(1)).save(any(CommentsModels.class));
        verify(emailService, times(1)).sendSimpleMessage(eq(assignee.getEmail()), anyString(), anyString());
    }

    @Test
    void createComment_whenValidRequestAndAssigneeIsCommenter_shouldReturnOkAndNotSendEmail() throws Exception {
        // Arrange
        card.setAssignee(user); // Assignee is the same as the commenter
        CommentRequest request = new CommentRequest();
        request.setCardId(cardId);
        request.setUserId(userId);
        request.setContent("New comment text");

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(CommentsModels.class))).thenReturn(comment);

        // Act & Assert
        mockMvc.perform(post("/api/v1/comments")
                        .header("Authorization", "Bearer " + validJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(commentRepository, times(1)).save(any(CommentsModels.class));
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    // ...existing code...
    @Test
    void createComment_whenCardNotFound_shouldReturnNotFound() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setCardId(cardId);
        request.setUserId(userId);
        request.setContent("New comment text");

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/v1/comments")
                        .header("Authorization", "Bearer " + validJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createComment_whenUserNotFound_shouldReturnNotFound() throws Exception {
        // Arrange
        CommentRequest request = new CommentRequest();
        request.setCardId(cardId);
        request.setUserId(userId);
        request.setContent("New comment text");

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/v1/comments")
                        .header("Authorization", "Bearer " + validJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(commentRepository, never()).save(any(CommentsModels.class));
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    void getCommentsByCardId_shouldReturnListOfComments() throws Exception {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card)); 
        
        when(commentRepository.findByCardId(cardId)).thenReturn(List.of(comment));

        mockMvc.perform(get("/api/v1/comments/card/{cardId}", cardId)
                        .header("Authorization", "Bearer " + validJwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(comment.getContent()));

        verify(commentRepository, times(1)).findByCardId(cardId);
    }
}
