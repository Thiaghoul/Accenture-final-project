package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.CardRequest;
import com.group5.taskFlow.dto.CardResponse;
import com.group5.taskFlow.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

  private final CardService cardService;

  @Autowired
  public CardController(CardService cardService) {
    this.cardService = cardService;
  }

  @PostMapping
  public ResponseEntity<CardResponse> createCard(@PathVariable UUID projectId, @RequestBody CardRequest cardRequest) {
    CardResponse createdCard = cardService.save(projectId, cardRequest);
    return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<CardResponse>> getAllCardsForProject(@PathVariable UUID projectId) {
    List<CardResponse> cards = cardService.findAllByProjectId(projectId);
    return new ResponseEntity<>(cards, HttpStatus.OK);
  }

  @GetMapping("/{taskId}")
  public ResponseEntity<CardResponse> getCardById(@PathVariable UUID projectId, @PathVariable UUID taskId) {
    CardResponse card = cardService.findById(taskId);
    return new ResponseEntity<>(card, HttpStatus.OK);
  }

  @PutMapping("/{taskId}")
  public ResponseEntity<CardResponse> updateCard(@PathVariable UUID projectId, @PathVariable UUID taskId, @RequestBody CardRequest cardRequest) {
    CardResponse updatedCard = cardService.update(taskId, cardRequest);
    return new ResponseEntity<>(updatedCard, HttpStatus.OK);
  }

  @PutMapping("/{taskId}/status/{newStatusId}")
  public ResponseEntity<CardResponse> changeCardStatus(@PathVariable UUID projectId, @PathVariable UUID taskId, @PathVariable UUID newStatusId) {
    cardService.changeStatusWithId(taskId, newStatusId);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<Void> deleteCard(@PathVariable UUID projectId, @PathVariable UUID taskId) {
    cardService.deleteById(taskId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
  
  @PostMapping("/{taskId}/assign-me")
  public ResponseEntity<CardResponse> assignMeToCard(@PathVariable UUID taskId, @RequestHeader("X-User-Id") UUID userId) {
    CardResponse updatedCard = cardService.assignMeToCard(taskId, userId);
    return new ResponseEntity<>(updatedCard, HttpStatus.OK);
  }
}