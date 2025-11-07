package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.AddMemberRequest;
import com.group5.taskFlow.dto.BoardRequest;
import com.group5.taskFlow.dto.BoardResponse;
import com.group5.taskFlow.dto.UserResponse;
import com.group5.taskFlow.model.BoardMembersModels;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.MemberRoles;
import com.group5.taskFlow.model.enums.EventType;
import com.group5.taskFlow.repository.BoardMembersRepository;
import com.group5.taskFlow.repository.BoardRepository;
import com.group5.taskFlow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardMembersRepository boardMembersRepository;
    private final ActivityLogService activityLogService;

    @Autowired
    public BoardService(BoardRepository boardRepository, UserRepository userRepository, BoardMembersRepository boardMembersRepository, ActivityLogService activityLogService) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.boardMembersRepository = boardMembersRepository;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public BoardResponse save(BoardRequest boardRequest, String username) {
        UserModels owner = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + username));

        BoardModels board = new BoardModels();
        board.setName(boardRequest.getName());
        board.setDescription(boardRequest.getDescription());
        board.setOwner(owner);
        board.setCreatedAt(Instant.now());
        board.setUpdatedAt(Instant.now());

        BoardModels savedBoard = boardRepository.save(board);
        activityLogService.logActivity(EventType.BOARD_CREATED, "Board created: " + savedBoard.getName(), owner, savedBoard);
        return toBoardResponse(savedBoard);
    }

    public List<BoardResponse> findAll(String username) {
        UserModels user = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + username));
        return boardRepository.findByOwner(user).stream()
                .map(this::toBoardResponse)
                .collect(Collectors.toList());
    }

    public BoardResponse findById(UUID id) {
        BoardModels board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));
        return toBoardResponse(board);
    }

    @Transactional
    public BoardResponse update(UUID id, BoardRequest boardRequest) {
        BoardModels board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));

        board.setName(boardRequest.getName());
        board.setDescription(boardRequest.getDescription());
        board.setUpdatedAt(Instant.now());

        BoardModels updatedBoard = boardRepository.save(board);
        activityLogService.logActivity(EventType.BOARD_UPDATED, "Board updated: " + updatedBoard.getName(), updatedBoard.getOwner(), updatedBoard);
        return toBoardResponse(updatedBoard);
    }

    @Transactional
    public void deleteById(UUID id) {
        BoardModels board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));
        activityLogService.logActivity(EventType.BOARD_DELETED, "Board deleted: " + board.getName(), board.getOwner(), board);
        boardRepository.deleteById(id);
    }

    @Transactional
    public void addMember(UUID boardId, UUID userId, MemberRoles role) {
        BoardModels board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + boardId));

        UserModels user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        if (boardMembersRepository.existsByBoardAndUser(board, user)) {
            throw new IllegalArgumentException("User is already a member of this board.");
        }

        BoardMembersModels newMember = new BoardMembersModels();
        newMember.setBoard(board);
        newMember.setUser(user);
        newMember.setRole(role);

        boardMembersRepository.save(newMember);
        activityLogService.logActivity(EventType.MEMBER_ADDED, "Member added: " + user.getEmail(), board.getOwner(), board);
    }

    public List<UserResponse> getMembers(UUID projectId) {
        BoardModels board = boardRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + projectId));

        return board.getMembers().stream()
                .map(member -> toUserResponse(member.getUser()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeMember(UUID projectId, UUID userId) {
        BoardModels board = boardRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + projectId));

        UserModels user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        BoardMembersModels member = boardMembersRepository.findByBoardAndUser(board, user)
                .orElseThrow(() -> new EntityNotFoundException("User is not a member of this board."));

        boardMembersRepository.delete(member);
        activityLogService.logActivity(EventType.MEMBER_REMOVED, "Member removed: " + user.getEmail(), board.getOwner(), board);
    }

    private BoardResponse toBoardResponse(BoardModels board) {
        BoardResponse boardResponse = new BoardResponse();
        boardResponse.setId(board.getId());
        boardResponse.setName(board.getName());
        boardResponse.setDescription(board.getDescription());
        boardResponse.setOwnerId(board.getOwner().getId());
        boardResponse.setCreatedAt(board.getCreatedAt());
        boardResponse.setUpdatedAt(board.getUpdatedAt());
        return boardResponse;
    }

    private UserResponse toUserResponse(UserModels user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        return userResponse;
    }
}