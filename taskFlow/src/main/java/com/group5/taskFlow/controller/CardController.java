package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.BoardResponse;
import com.group5.taskFlow.dto.CardRequest;
import com.group5.taskFlow.dto.CardResponse;
import com.group5.taskFlow.service.BoardService;
import com.group5.taskFlow.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cards")
public class CardController {

  private final CardService cardService;

  public CardController(CardService cardService) {
    this.cardService = cardService;
  }

  @PostMapping
  public ResponseEntity<CardResponse> createCard(@RequestBody CardRequest cardRequest) {
    CardResponse newCard = cardService.save(cardRequest);
    return new ResponseEntity<>(newCard, HttpStatus.CREATED);
  }

  @GetMapping("/board/{id}")
  public ResponseEntity<List<CardResponse>> getAllCardsOfBoard(@PathVariable UUID id) {
    var result = cardService.getAllCardOfBoard(id);
    return  new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PutMapping("/{id}/complete")
  public ResponseEntity<CardResponse> completeCard(@PathVariable UUID id) {
    cardService.changeStatusWithId(id);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<CardResponse> deleteCard(@PathVariable UUID id) {
    cardService.deleteById(id);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }
}