package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.*;
import com.group5.taskFlow.service.BoardService;
import com.group5.taskFlow.service.CardService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class BoardController {
    private static final Logger log = LoggerFactory.getLogger(BoardController.class);

    private final BoardService boardService;
    private final CardService cardService;

    public BoardController(BoardService boardService, CardService cardService) {
        this.boardService = boardService;
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@Valid @RequestBody BoardRequest boardRequest, Principal principal) {
        log.info("Received request to create board with name: {}", boardRequest.getName());
        BoardResponse newBoard = boardService.save(boardRequest, principal.getName());
        log.info("Responding with created board: {}", newBoard);
        return new ResponseEntity<>(newBoard, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BoardResponse>> getAllBoards(Principal principal) {
        log.info("Received request to get all boards for user: {}", principal.getName());
        List<BoardResponse> boards = boardService.findAll(principal.getName());
        log.info("Responding with {} boards", boards.size());
        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionService.isBoardMember(principal.name, #id)")
    public ResponseEntity<BoardResponse> getBoardById(@PathVariable UUID id) {
        log.info("Received request to get board with id: {}", id);
        BoardResponse board = boardService.findById(id);
        log.info("Responding with board: {}", board);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionService.isBoardOwner(principal.name, #id)")
    public ResponseEntity<BoardResponse> updateBoard(@PathVariable UUID id, @RequestBody BoardRequest boardRequest) {
        log.info("Received request to update board with id: {}", id);
        BoardResponse updatedBoard = boardService.update(id, boardRequest);
        log.info("Responding with updated board: {}", updatedBoard);
        return new ResponseEntity<>(updatedBoard, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionService.isBoardOwner(principal.name, #id)")
    public ResponseEntity<Void> deleteBoard(@PathVariable UUID id) {
        log.info("Received request to delete board with id: {}", id);
        boardService.deleteById(id);
        log.info("Board with id: {} deleted successfully", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Member Endpoints
    @GetMapping("/{projectId}/members")
    @PreAuthorize("@permissionService.isBoardMember(principal.name, #projectId)")
    public ResponseEntity<List<UserResponse>> getProjectMembers(@PathVariable UUID projectId) {
        log.info("Received request to get members for project with id: {}", projectId);
        List<UserResponse> members = boardService.getMembers(projectId);
        log.info("Responding with {} members", members.size());
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{projectId}/members")
    @PreAuthorize("@permissionService.isBoardOwner(principal.name, #projectId)")
    public ResponseEntity<Void> addProjectMember(@PathVariable UUID projectId, @RequestBody AddMemberRequest addMemberRequest) {
        log.info("Received request to add member with id: {} to project with id: {}", addMemberRequest.getUserId(), projectId);
        boardService.addMember(projectId, addMemberRequest.getUserId(), addMemberRequest.getRole());
        log.info("Member with id: {} added to project with id: {} successfully", addMemberRequest.getUserId(), projectId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    @PreAuthorize("@permissionService.isBoardOwner(principal.name, #projectId)")
    public ResponseEntity<Void> removeProjectMember(@PathVariable UUID projectId, @PathVariable UUID userId) {
        log.info("Received request to remove member with id: {} from project with id: {}", userId, projectId);
        boardService.removeMember(projectId, userId);
        log.info("Member with id: {} removed from project with id: {} successfully", userId, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
