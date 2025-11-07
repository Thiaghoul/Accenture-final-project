package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.BoardMemberRequest;
import com.group5.taskFlow.dto.BoardMemberResponse;
import com.group5.taskFlow.dto.BoardResponse;
import com.group5.taskFlow.model.BoardMembersModels;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.MemberRoles;
import com.group5.taskFlow.repository.BoardMemberRepository;
import com.group5.taskFlow.repository.BoardRepository;
import com.group5.taskFlow.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
  private final EmailService emailService;

  public BoardMemberService(BoardMemberRepository boardMemberRepository, UserRepository userRepository, BoardRepository boardRepository, EmailService emailService) {
    this.boardMemberRepository = boardMemberRepository;
    this.userRepository = userRepository;
    this.boardRepository = boardRepository;
    this.emailService = emailService;
  }

  public List<BoardModels> findBoardsByUserId(UUID userId) {
    log.info("Finding boards for user with id: {}", userId);
    List<BoardMembersModels> boardMembers = boardMemberRepository.findByUserId(userId);

    return boardMembers.stream()
            .map(BoardMembersModels::getBoard)
            .toList();
  }

  public BoardMemberResponse createBoardMember(BoardMemberRequest boardMemberRequest) {
    log.info("Adding user {} to board {}", boardMemberRequest.getUserId(), boardMemberRequest.getBoardId());
    UserModels user = userRepository.findById(boardMemberRequest.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
    BoardModels board = boardRepository.findById(boardMemberRequest.getBoardId())
            .orElseThrow(() -> new RuntimeException("Board not found"));

    BoardMembersModels boardMember = new BoardMembersModels();
    boardMember.setUser(user);
    boardMember.setBoard(board);
    boardMember.setRole(MemberRoles.valueOf(boardMemberRequest.getMemberRole()));

    BoardMembersModels savedBoardMember = boardMemberRepository.save(boardMember);

    String message = String.format("You have been invited to the board: %s", board.getName());
    emailService.sendSimpleMessage(user.getEmail(), "You have been invited to a board", message);
    log.info("Invitation email sent to {}", user.getEmail());

    BoardMemberResponse response = new BoardMemberResponse();
//        response.setId(savedBoardMember.getId());
    response.setUserId(savedBoardMember.getUser().getId());
    response.setBoardId(savedBoardMember.getBoard().getId());
    response.setMemberRole(savedBoardMember.getRole().name());
    return response;
  }
}
