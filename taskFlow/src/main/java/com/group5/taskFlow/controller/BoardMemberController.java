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

        try {
            List<BoardModels> boards = boardMemberService.findBoardsByUserId(userId);
            var listResult = boards.stream().map(boardService::toBoardResponse).toList();

            // TODO: problema em enviar a lista de boards para o cliente
            return ResponseEntity.ok().body(listResult);
        } catch (Exception e) {
            System.out.println("ERROR -- " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/board/{boardId}/members")
    public ResponseEntity<List<BoardMemberResponse>> getBoardMembers(@PathVariable UUID boardId) {
        log.info("Received request to get members for board id: {}", boardId);
        List<BoardMemberResponse> members = boardMemberService.findBoardMembers(boardId);
        return ResponseEntity.ok(members);
    }

    @DeleteMapping("/board/{boardId}/members/{userId}")
    public ResponseEntity<Void> removeBoardMember(@PathVariable UUID boardId, @PathVariable UUID userId) {
        log.info("Received request to remove member {} from board {}", userId, boardId);
        boardMemberService.removeBoardMember(boardId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<BoardMemberResponse> createBoardMember(@RequestBody BoardMemberRequest boardMemberRequest) {
        log.info("Received request to create board member");
        BoardMemberResponse newBoardMember = boardMemberService.createBoardMember(boardMemberRequest);
        return ResponseEntity.ok(newBoardMember);
    }
}
