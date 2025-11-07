package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.BoardRequest;
import com.group5.taskFlow.dto.BoardResponse;
import com.group5.taskFlow.model.BoardMembersModels;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.MemberRoles;
import com.group5.taskFlow.repository.BoardMembersRepository;
import com.group5.taskFlow.repository.BoardRepository;
import com.group5.taskFlow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardMembersRepository boardMembersRepository;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private BoardService boardService;

    private BoardModels boardModels;
    private BoardRequest boardRequest;
    private BoardResponse boardResponse;
    private UserModels owner;

    @BeforeEach
    void setUp() {
        owner = new UserModels();
        owner.setId(UUID.randomUUID());
        owner.setEmail("owner@example.com");

        boardModels = new BoardModels();
        boardModels.setId(UUID.randomUUID());
        boardModels.setName("Test Board");
        boardModels.setDescription("Description for test board");
        boardModels.setCreatedAt(Instant.now());
        boardModels.setUpdatedAt(Instant.now());
        boardModels.setOwner(owner);

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
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(owner));
        when(boardRepository.save(any(BoardModels.class))).thenReturn(boardModels);

        BoardResponse result = boardService.save(boardRequest, owner.getEmail());

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(boardRequest.getName());
        assertThat(result.getDescription()).isEqualTo(boardRequest.getDescription());
    }
}
