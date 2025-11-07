package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.BoardMemberRequest;
import com.group5.taskFlow.dto.BoardMemberResponse;
import com.group5.taskFlow.dto.BoardResponse;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.service.BoardMemberService;
import com.group5.taskFlow.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/board-members")
@Slf4j
public class BoardMemberController {

  private final BoardMemberService boardMemberService;
  private final BoardService boardService;

  public BoardMemberController(BoardMemberService boardMemberService, BoardService boardService) {
    this.boardMemberService = boardMemberService;
    this.boardService = boardService;
  }

  @GetMapping("/user/{userId}/boards")
  public ResponseEntity<List<BoardResponse>> getBoardsByUserId(@PathVariable UUID userId) {
    log.info("Received request to get boards for user id: {}", userId);
    List<BoardModels> boards = boardMemberService.findBoardsByUserId(userId);
    var listResult = boards.stream().map(boardService::toBoardResponse).toList();

    // TODO: problema em enviar a lista de boards para o cliente
    return ResponseEntity.ok().body(listResult);
  }

  @PostMapping
  public ResponseEntity<BoardMemberResponse> createBoardMember(@RequestBody BoardMemberRequest boardMemberRequest) {
    log.info("Received request to create board member");
    BoardMemberResponse newBoardMember = boardMemberService.createBoardMember(boardMemberRequest);
    return ResponseEntity.ok(newBoardMember);
  }
}
