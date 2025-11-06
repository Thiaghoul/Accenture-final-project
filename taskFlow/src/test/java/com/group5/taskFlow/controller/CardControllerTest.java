package com.group5.taskFlow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group5.taskFlow.dto.CardRequest;
import com.group5.taskFlow.dto.CardResponse;
import com.group5.taskFlow.security.JwtTokenProvider;
import com.group5.taskFlow.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = CardController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private CardRequest cardRequest;
    private CardResponse cardResponse;

    @BeforeEach
    void setUp() {
        cardRequest = new CardRequest();
        cardRequest.setTitle("Test Card");
        cardRequest.setDescription("Test Description");

        cardResponse = new CardResponse();
        cardResponse.setId(UUID.randomUUID());
        cardResponse.setTitle("Test Card");
        cardResponse.setDescription("Test Description");
    }

    @Test
    void createCard_shouldReturnCreated() throws Exception {
        when(cardService.save(any(CardRequest.class))).thenReturn(cardResponse);

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(cardResponse.getTitle()));
    }

    @Test
    void getAllCards_shouldReturnOk() throws Exception {
        List<CardResponse> cards = Collections.singletonList(cardResponse);
        when(cardService.findAll()).thenReturn(cards);

        mockMvc.perform(get("/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(cardResponse.getTitle()));
    }

    @Test
    void getCardById_shouldReturnOk() throws Exception {
        when(cardService.findById(any(UUID.class))).thenReturn(cardResponse);

        mockMvc.perform(get("/cards/{id}", cardResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(cardResponse.getTitle()));
    }

    @Test
    void updateCard_shouldReturnOk() throws Exception {
        when(cardService.update(any(UUID.class), any(CardRequest.class))).thenReturn(cardResponse);

        mockMvc.perform(put("/cards/{id}", cardResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(cardResponse.getTitle()));
    }

    @Test
    void deleteCard_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/cards/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }
}
