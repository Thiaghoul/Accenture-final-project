package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.BoardMemberRequest;
import com.group5.taskFlow.dto.BoardMemberResponse;
import com.group5.taskFlow.model.BoardMembersModels;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.repository.BoardMemberRepository;
import com.group5.taskFlow.repository.BoardRepository;
import com.group5.taskFlow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BoardMemberService {

    private final BoardMemberRepository boardMemberRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public BoardMemberService(BoardMemberRepository boardMemberRepository, UserRepository userRepository, BoardRepository boardRepository) {
        this.boardMemberRepository = boardMemberRepository;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
    }

    public List<BoardModels> findBoardsByUserId(UUID userId) {
        log.info("Finding boards for user id: {}", userId);
        List<BoardMembersModels> boardMembers = boardMemberRepository.findByUserId(userId);
        return boardMembers.stream()
                .map(BoardMembersModels::getBoard)
                .collect(Collectors.toList());
    }

    public List<BoardMemberResponse> findBoardMembers(UUID boardId) {
        return boardMemberRepository.findByBoardId(boardId).stream()
                .map(this::toBoardMemberResponse)
                .toList();
    }

    public void removeBoardMember(UUID boardId, UUID userId) {
        boardMemberRepository.deleteByBoardIdAndUserId(boardId, userId);
    }

    public BoardMemberResponse createBoardMember(BoardMemberRequest boardMemberRequest) {
        log.info("Creating board member with user id: {} and board id: {}", boardMemberRequest.getUserId(), boardMemberRequest.getBoardId());
        UserModels user = userRepository.findById(boardMemberRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + boardMemberRequest.getUserId()));
        log.info("User found with id: {}", user.getId());
        BoardModels board = boardRepository.findById(boardMemberRequest.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + boardMemberRequest.getBoardId()));
        log.info("Board found with id: {}", board.getId());
        BoardMembersModels boardMember = new BoardMembersModels();
        boardMember.setUser(user);
        boardMember.setBoard(board);
        boardMember.setRole(boardMemberRequest.getRole());
        log.info("Board member role set to: {}", boardMember.getRole());
        BoardMembersModels savedBoardMember = boardMemberRepository.save(boardMember);
        log.info("Board member saved with id: {}", savedBoardMember.getId());
        return toBoardMemberResponse(savedBoardMember);
    }

    private BoardMemberResponse toBoardMemberResponse(BoardMembersModels boardMember) {
        BoardMemberResponse boardMemberResponse = new BoardMemberResponse();
        boardMemberResponse.setUserId(boardMember.getUser().getId());
        boardMemberResponse.setBoardId(boardMember.getBoard().getId());
        boardMemberResponse.setRole(boardMember.getRole());
        return boardMemberResponse;
    }
}
