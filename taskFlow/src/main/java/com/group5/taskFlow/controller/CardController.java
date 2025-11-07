package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.CardRequest;
import com.group5.taskFlow.dto.CardResponse;
import com.group5.taskFlow.service.CardService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {
    private static final Logger log = LoggerFactory.getLogger(CardController.class);

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    @PreAuthorize("@permissionService.isBoardMember(principal.name, #projectId)")
    public ResponseEntity<CardResponse> createCard(@PathVariable UUID projectId, @RequestBody CardRequest cardRequest) {
        log.info("Received request to create task in project with id: {}", projectId);
        CardResponse newTask = cardService.save(projectId, cardRequest);
        log.info("Responding with created task: {}", newTask);
        return new ResponseEntity<>(newTask, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("@permissionService.isBoardMember(principal.name, #projectId)")
    public ResponseEntity<List<CardResponse>> getAllCardsForProject(@PathVariable UUID projectId) {
        log.info("Received request to get all tasks for project with id: {}", projectId);
        List<CardResponse> tasks = cardService.findAllByProjectId(projectId);
        log.info("Responding with {} tasks", tasks.size());
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("@permissionService.isBoardMember(principal.name, #projectId)")
    public ResponseEntity<CardResponse> getCardById(@PathVariable UUID projectId, @PathVariable UUID taskId) {
        log.info("Received request to get task with id: {} for project with id: {}", taskId, projectId);
        CardResponse card = cardService.findById(taskId);
        log.info("Responding with task: {}", card);
        return new ResponseEntity<>(card, HttpStatus.OK);
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("@permissionService.isBoardMember(principal.name, #projectId)")
    public ResponseEntity<CardResponse> updateCard(@PathVariable UUID projectId, @PathVariable UUID taskId, @RequestBody CardRequest cardRequest) {
        log.info("Received request to update task with id: {} for project with id: {}", taskId, projectId);
        CardResponse updatedCard = cardService.update(taskId, cardRequest);
        log.info("Responding with updated task: {}", updatedCard);
        return new ResponseEntity<>(updatedCard, HttpStatus.OK);
    }

    @PutMapping("/{taskId}/status/{newStatusId}")
    @PreAuthorize("@permissionService.isBoardMember(principal.name, #projectId)")
    public ResponseEntity<CardResponse> changeCardStatus(@PathVariable UUID projectId, @PathVariable UUID taskId, @PathVariable UUID newStatusId) {
        log.info("Received request to change status of task with id: {} to column with id: {}", taskId, newStatusId);
        cardService.changeStatusWithId(taskId, newStatusId);
        log.info("Status of task with id: {} changed successfully", taskId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("@permissionService.isBoardOwner(principal.name, #projectId)")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID projectId, @PathVariable UUID taskId) {
        log.info("Received request to delete task with id: {} for project with id: {}", taskId, projectId);
        cardService.deleteById(taskId);
        log.info("Task with id: {} deleted successfully", taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{taskId}/assign-me")
    @PreAuthorize("@permissionService.isBoardMember(principal.name, #projectId)")
    public ResponseEntity<CardResponse> assignMeToCard(@PathVariable UUID projectId, @PathVariable UUID taskId, @RequestHeader("X-User-Id") UUID userId) {
        log.info("Received request to assign user with id: {} to task with id: {}", userId, taskId);
        CardResponse updatedCard = cardService.assignMeToCard(taskId, userId);
        log.info("User with id: {} assigned to task with id: {} successfully", userId, taskId);
        return new ResponseEntity<>(updatedCard, HttpStatus.OK);
    }
}