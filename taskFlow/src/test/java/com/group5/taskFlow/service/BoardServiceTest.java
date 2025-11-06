package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.BoardRequest;
import com.group5.taskFlow.dto.BoardResponse;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.repository.BoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;

    private BoardModels boardModels;
    private BoardRequest boardRequest;
    private BoardResponse boardResponse;

    @BeforeEach
    void setUp() {
        boardModels = new BoardModels();
        boardModels.setId(UUID.randomUUID());
        boardModels.setName("Test Board");
        boardModels.setDescription("Description for test board");
        boardModels.setCreatedAt(Instant.now());
        boardModels.setUpdatedAt(Instant.now());

        boardRequest = new BoardRequest();
        boardRequest.setName("Test Board");
        boardRequest.setDescription("Description for test board");

        boardResponse = new BoardResponse();
        boardResponse.setId(boardModels.getId());
        boardResponse.setName("Test Board");
        boardResponse.setDescription("Description for test board");
        boardResponse.setCreatedAt(boardModels.getCreatedAt());
        boardResponse.setUpdatedAt(boardModels.getUpdatedAt());
    }

    @Test
    public void save_shouldReturnBoardResponse() {
        when(boardRepository.save(any(BoardModels.class))).thenReturn(boardModels);

        BoardResponse result = boardService.save(boardRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(boardRequest.getName());
        assertThat(result.getDescription()).isEqualTo(boardRequest.getDescription());
    }
}
