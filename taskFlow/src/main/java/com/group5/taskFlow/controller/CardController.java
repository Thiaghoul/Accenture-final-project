package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.CardRequest;
import com.group5.taskFlow.dto.CardResponse;
import com.group5.taskFlow.service.CardService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cards")
@Slf4j
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardRequest cardRequest) {
        log.info("Received request to create card with title: {}", cardRequest.getTitle());
        CardResponse newCard = cardService.save(cardRequest);
        return new ResponseEntity<>(newCard, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> getAllCards() {
        log.info("Received request to get all cards");
        List<CardResponse> cards = cardService.findAll();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCardById(@PathVariable UUID id) {
        log.info("Received request to get card with id: {}", id);
        CardResponse card = cardService.findById(id);
        return ResponseEntity.ok(card);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardResponse> updateCard(@PathVariable UUID id, @Valid @RequestBody CardRequest cardRequest) {
        log.info("Received request to update card with id: {}", id);
        CardResponse updatedCard = cardService.update(id, cardRequest);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID id) {
        log.info("Received request to delete card with id: {}", id);
        cardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}