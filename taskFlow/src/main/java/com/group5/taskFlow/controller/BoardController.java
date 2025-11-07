package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.BoardRequest;
import com.group5.taskFlow.dto.BoardResponse;
import com.group5.taskFlow.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@RequestBody BoardRequest boardRequest) {
        BoardResponse newBoard = boardService.save(boardRequest);
        return new ResponseEntity<>(newBoard, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoardById(@PathVariable UUID id) {
        BoardResponse board = boardService.findById(id);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BoardResponse>> getAllBoards() {
        List<BoardResponse> boards = boardService.findAll();
        return new ResponseEntity<>(boards, HttpStatus.OK);
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
