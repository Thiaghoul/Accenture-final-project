package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.*;
import com.group5.taskFlow.model.enums.MemberRoles;
import com.group5.taskFlow.service.BoardService;
import com.group5.taskFlow.service.CardService;
import jakarta.validation.Valid;
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
    @PreAuthorize("@permissionService.isBoardMember(principal.name, #id)")
    public ResponseEntity<BoardResponse> getBoardById(@PathVariable UUID id) {
        BoardResponse board = boardService.findById(id);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionService.isBoardOwner(principal.name, #id)")
    public ResponseEntity<BoardResponse> updateBoard(@PathVariable UUID id, @RequestBody BoardRequest boardRequest) {
        BoardResponse updatedBoard = boardService.update(id, boardRequest);
        return new ResponseEntity<>(updatedBoard, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionService.isBoardOwner(principal.name, #id)")
    public ResponseEntity<Void> deleteBoard(@PathVariable UUID id) {
        boardService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Member Endpoints
    @GetMapping("/{projectId}/members")
    @PreAuthorize("@permissionService.isBoardMember(principal.name, #projectId)")
    public ResponseEntity<List<UserResponse>> getProjectMembers(@PathVariable UUID projectId) {
        List<UserResponse> members = boardService.getMembers(projectId);
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{projectId}/members")
    @PreAuthorize("@permissionService.isBoardOwner(principal.name, #projectId)")
    public ResponseEntity<Void> addProjectMember(@PathVariable UUID projectId, @RequestBody AddMemberRequest addMemberRequest) {
        boardService.addMember(projectId, addMemberRequest.getUserId(), addMemberRequest.getRole());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    @PreAuthorize("@permissionService.isBoardOwner(principal.name, #projectId)")
    public ResponseEntity<Void> removeProjectMember(@PathVariable UUID projectId, @PathVariable UUID userId) {
        boardService.removeMember(projectId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
