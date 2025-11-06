package com.group5.taskFlow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group5.taskFlow.dto.BoardRequest;
import com.group5.taskFlow.dto.BoardResponse;
import com.group5.taskFlow.security.JwtTokenProvider;
import com.group5.taskFlow.service.BoardService;
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

@WebMvcTest(value = BoardController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private BoardRequest boardRequest;
    private BoardResponse boardResponse;

    @BeforeEach
    void setUp() {
        boardRequest = new BoardRequest();
        boardRequest.setName("Test Board");

        boardResponse = new BoardResponse();
        boardResponse.setId(UUID.randomUUID());
        boardResponse.setName("Test Board");
    }

    @Test
    void createBoard_shouldReturnCreated() throws Exception {
        when(boardService.save(any(BoardRequest.class))).thenReturn(boardResponse);

        mockMvc.perform(post("/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(boardRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(boardResponse.getName()));
    }

    @Test
    void getAllBoards_shouldReturnOk() throws Exception {
        List<BoardResponse> boards = Collections.singletonList(boardResponse);
        when(boardService.findAll()).thenReturn(boards);

        mockMvc.perform(get("/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(boardResponse.getName()));
    }

    @Test
    void getBoardById_shouldReturnOk() throws Exception {
        when(boardService.findById(any(UUID.class))).thenReturn(boardResponse);

        mockMvc.perform(get("/boards/{id}", boardResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(boardResponse.getName()));
    }

    @Test
    void updateBoard_shouldReturnOk() throws Exception {
        when(boardService.update(any(UUID.class), any(BoardRequest.class))).thenReturn(boardResponse);

        mockMvc.perform(put("/boards/{id}", boardResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(boardRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(boardResponse.getName()));
    }

    @Test
    void deleteBoard_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/boards/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }
}