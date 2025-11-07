package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.BoardRequest;
import com.group5.taskFlow.dto.BoardResponse;
import com.group5.taskFlow.dto.CardRequest;
import com.group5.taskFlow.dto.CardResponse;
import com.group5.taskFlow.service.BoardService;
import com.group5.taskFlow.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class BoardController {

    private final BoardService boardService;
    private final CardService cardService;

    public BoardController(BoardService boardService, CardService cardService) {
        this.boardService = boardService;
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@Valid @RequestBody BoardRequest boardRequest, Principal principal) {
        BoardResponse newBoard = boardService.save(boardRequest, principal.getName());
        return new ResponseEntity<>(newBoard, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BoardResponse>> getAllBoards(Principal principal) {
        List<BoardResponse> boards = boardService.findAll(principal.getName());
        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoardById(@PathVariable UUID id) {
        BoardResponse board = boardService.findById(id);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<CardResponse>> getTasksForBoard(@PathVariable UUID projectId) {
        List<CardResponse> tasks = boardService.getTasksForBoard(projectId);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<CardResponse> createTaskForBoard(@PathVariable UUID projectId, @RequestBody CardRequest cardRequest) {
        CardResponse newCard = cardService.save(projectId, cardRequest);
        return new ResponseEntity<>(newCard, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardResponse> updateBoard(@PathVariable UUID id, @RequestBody BoardRequest boardRequest) {
        BoardResponse updatedBoard = boardService.update(id, boardRequest);
        return new ResponseEntity<>(updatedBoard, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable UUID id) {
        boardService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
