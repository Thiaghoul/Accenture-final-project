package com.group5.taskFlow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group5.taskFlow.dto.CardRequest;
import com.group5.taskFlow.dto.CardResponse;
import com.group5.taskFlow.security.JwtTokenProvider;
import com.group5.taskFlow.service.CardService;
import com.group5.taskFlow.service.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID projectId;
    private CardRequest cardRequest;
    private CardResponse cardResponse;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();

        cardRequest = new CardRequest();
        cardRequest.setTitle("Test Card");
        cardRequest.setDescription("Test Description");

        cardResponse = new CardResponse();
        cardResponse.setId(UUID.randomUUID());
        cardResponse.setTitle("Test Card");
        cardResponse.setDescription("Test Description");
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = {"USER"})
    void createCard_shouldReturnCreated() throws Exception {
        when(permissionService.isBoardMember(anyString(), eq(projectId))).thenReturn(true);
        when(cardService.save(eq(projectId), any(CardRequest.class))).thenReturn(cardResponse);

        mockMvc.perform(post("/api/v1/cards", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(cardResponse.getTitle()));
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = {"USER"})
    void getAllCardsForProject_shouldReturnOk() throws Exception {
        List<CardResponse> cards = Collections.singletonList(cardResponse);
        when(permissionService.isBoardMember(anyString(), eq(projectId))).thenReturn(true);
        when(cardService.findAllByProjectId(eq(projectId))).thenReturn(cards);

        mockMvc.perform(get("/api/v1/cards", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(cardResponse.getTitle()));
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = {"USER"})
    void getCardById_shouldReturnOk() throws Exception {
        when(permissionService.isBoardMember(anyString(), eq(projectId))).thenReturn(true);
        when(cardService.findById(any(UUID.class))).thenReturn(cardResponse);

        mockMvc.perform(get("/api/v1/cards/{taskId}", projectId, cardResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(cardResponse.getTitle()));
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = {"USER"})
    void updateCard_shouldReturnOk() throws Exception {
        when(permissionService.isBoardMember(anyString(), eq(projectId))).thenReturn(true);
        when(cardService.update(any(UUID.class), any(CardRequest.class))).thenReturn(cardResponse);

        mockMvc.perform(put("/api/v1/cards/{taskId}", projectId, cardResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(cardResponse.getTitle()));
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = {"USER"})
    void changeCardStatus_shouldReturnAccepted() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID newStatusId = UUID.randomUUID();
        when(permissionService.isBoardMember(anyString(), eq(projectId))).thenReturn(true);
        mockMvc.perform(put("/api/v1/cards/{taskId}/status/{newStatusId}", projectId, taskId, newStatusId))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = {"USER"})
    void deleteCard_shouldReturnNoContent() throws Exception {
        when(permissionService.isBoardOwner(anyString(), eq(projectId))).thenReturn(true);
        mockMvc.perform(delete("/api/v1/cards/{taskId}", projectId, UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = {"USER"})
    void assignMeToCard_shouldReturnOk() throws Exception {
        UUID cardId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(permissionService.isBoardMember(anyString(), eq(projectId))).thenReturn(true);
        when(cardService.assignMeToCard(cardId, userId)).thenReturn(cardResponse);

        mockMvc.perform(post("/api/v1/cards/{taskId}/assign-me", projectId, cardId)
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(cardResponse.getTitle()));
    }
}
