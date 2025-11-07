package com.group5.taskFlow.service;

import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.repository.BoardRepository;
import com.group5.taskFlow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PermissionService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    @Autowired
    public PermissionService(UserRepository userRepository, BoardRepository boardRepository) {
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
    }

    public boolean isBoardOwner(String username, UUID boardId) {
        UserModels user = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + username));
        BoardModels board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + boardId));
        return board.getOwner().equals(user);
    }

    public boolean isBoardMember(String username, UUID boardId) {
        UserModels user = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + username));
        BoardModels board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + boardId));
        return board.getMembers().stream().anyMatch(member -> member.getUser().equals(user)) || isBoardOwner(username, boardId);
    }
}
