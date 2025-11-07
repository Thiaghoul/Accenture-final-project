package com.group5.taskFlow.service;

import com.group5.taskFlow.model.BoardMembersModels;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.repository.BoardRepository;
import com.group5.taskFlow.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private PermissionService permissionService;

    @Test
    void isBoardOwner() {
        UserModels user = new UserModels();
        user.setId(UUID.randomUUID());
        user.setEmail("test@test.com");

        BoardModels board = new BoardModels();
        board.setId(UUID.randomUUID());
        board.setOwner(user);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        assertTrue(permissionService.isBoardOwner("test@test.com", board.getId()));
    }

    @Test
    void isBoardMember() {
        UserModels user = new UserModels();
        user.setId(UUID.randomUUID());
        user.setEmail("test@test.com");

        BoardModels board = new BoardModels();
        board.setId(UUID.randomUUID());
        board.setOwner(new UserModels());

        BoardMembersModels member = new BoardMembersModels();
        member.setUser(user);
        board.setMembers(Collections.singleton(member));

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        assertTrue(permissionService.isBoardMember("test@test.com", board.getId()));
    }

    @Test
    void isNotBoardMember() {
        UserModels user = new UserModels();
        user.setId(UUID.randomUUID());
        user.setEmail("test@test.com");

        BoardModels board = new BoardModels();
        board.setId(UUID.randomUUID());
        board.setOwner(new UserModels());

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        assertFalse(permissionService.isBoardMember("test@test.com", board.getId()));
    }
}
