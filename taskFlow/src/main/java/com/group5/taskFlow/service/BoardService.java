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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BoardService {
    private static final Logger log = LoggerFactory.getLogger(BoardService.class);

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
        log.info("Attempting to save board with name: {} for user: {}", boardRequest.getName(), username);
        UserModels owner = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", username);
                    return new EntityNotFoundException("User not found with email: " + username);
                });

        BoardModels board = new BoardModels();
        board.setName(boardRequest.getName());
        board.setDescription(boardRequest.getDescription());
        board.setOwner(owner);

        BoardMembersModels ownerMembership = new BoardMembersModels();
        ownerMembership.setBoard(board);
        ownerMembership.setUser(owner);
        ownerMembership.setRole(MemberRoles.OWNER);
        board.getMembers().add(ownerMembership);

        BoardModels savedBoard = boardRepository.save(board);
        log.info("Board with id: {} saved successfully", savedBoard.getId());
        
        activityLogService.logActivity(EventType.BOARD_CREATED, "Board created: " + savedBoard.getName(), owner, savedBoard);
        return toBoardResponse(savedBoard);
    }

    public List<BoardResponse> findAll(String username) {
        log.info("Fetching all boards for user: {}", username);
        UserModels user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", username);
                    return new EntityNotFoundException("User not found with email: " + username);
                });
        List<BoardModels> boards = boardRepository.findByOwner(user);
        log.info("Found {} boards for user: {}", boards.size(), username);
        return boards.stream()
                .map(this::toBoardResponse)
                .collect(Collectors.toList());
    }

    public BoardResponse findById(UUID id) {
        log.info("Fetching board with id: {}", id);
        BoardModels board = boardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Board not found with id: {}", id);
                    return new EntityNotFoundException("Board not found with id: " + id);
                });
        return toBoardResponse(board);
    }

    @Transactional
    public BoardResponse update(UUID id, BoardRequest boardRequest) {
        log.info("Updating board with id: {}", id);
        BoardModels board = boardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Board not found with id: {}", id);
                    return new EntityNotFoundException("Board not found with id: " + id);
                });

        board.setName(boardRequest.getName());
        board.setDescription(boardRequest.getDescription());
        board.setUpdatedAt(Instant.now());

        BoardModels updatedBoard = boardRepository.save(board);
        log.info("Board with id: {} updated successfully", id);
        activityLogService.logActivity(EventType.BOARD_UPDATED, "Board updated: " + updatedBoard.getName(), updatedBoard.getOwner(), updatedBoard);
        return toBoardResponse(updatedBoard);
    }

    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting board with id: {}", id);
        BoardModels board = boardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Board not found with id: {}", id);
                    return new EntityNotFoundException("Board not found with id: " + id);
                });
        activityLogService.logActivity(EventType.BOARD_DELETED, "Board deleted: " + board.getName(), board.getOwner(), board);
        boardRepository.deleteById(id);
        log.info("Board with id: {} deleted successfully", id);
    }

    @Transactional
    public void addMember(UUID boardId, UUID userId, MemberRoles role) {
        log.info("Adding member with id: {} to board with id: {}", userId, boardId);
        BoardModels board = boardRepository.findById(boardId)
                .orElseThrow(() -> {
                    log.error("Board not found with id: {}", boardId);
                    return new EntityNotFoundException("Board not found with id: " + boardId);
                });

        UserModels user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new EntityNotFoundException("User not found with id: " + userId);
                });

        if (boardMembersRepository.existsByBoardAndUser(board, user)) {
            log.warn("User with id: {} is already a member of board with id: {}", userId, boardId);
            throw new IllegalArgumentException("User is already a member of this board.");
        }

        BoardMembersModels newMember = new BoardMembersModels();
        newMember.setBoard(board);
        newMember.setUser(user);
        newMember.setRole(role);

        boardMembersRepository.save(newMember);
        log.info("Member with id: {} added to board with id: {} successfully", userId, boardId);
        activityLogService.logActivity(EventType.MEMBER_ADDED, "Member added: " + user.getEmail(), board.getOwner(), board);
    }

    public List<UserResponse> getMembers(UUID projectId) {
        log.info("Fetching members for project with id: {}", projectId);
        BoardModels board = boardRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("Board not found with id: {}", projectId);
                    return new EntityNotFoundException("Board not found with id: " + projectId);
                });

        List<UserResponse> members = board.getMembers().stream()
                .map(member -> toUserResponse(member.getUser()))
                .collect(Collectors.toList());
        log.info("Found {} members for project with id: {}", members.size(), projectId);
        return members;
    }

    @Transactional
    public void removeMember(UUID projectId, UUID userId) {
        log.info("Removing member with id: {} from project with id: {}", userId, projectId);
        BoardModels board = boardRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("Board not found with id: {}", projectId);
                    return new EntityNotFoundException("Board not found with id: " + projectId);
                });

        UserModels user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new EntityNotFoundException("User not found with id: " + userId);
                });

        BoardMembersModels member = boardMembersRepository.findByBoardAndUser(board, user)
                .orElseThrow(() -> {
                    log.error("User with id: {} is not a member of board with id: {}", userId, projectId);
                    return new EntityNotFoundException("User is not a member of this board.");
                });

        boardMembersRepository.delete(member);
        log.info("Member with id: {} removed from project with id: {} successfully", userId, projectId);
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