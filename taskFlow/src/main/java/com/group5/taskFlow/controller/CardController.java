package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.CardRequest;
import com.group5.taskFlow.dto.CardResponse;
import com.group5.taskFlow.service.CardService;
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

    @GetMapping
    public ResponseEntity<List<CardResponse>> getAllCards() {
        List<CardResponse> cards = cardService.findAll();
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCardById(@PathVariable UUID id) {
        CardResponse card = cardService.findById(id);
        return new ResponseEntity<>(card, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardResponse> updateCard(@PathVariable UUID id, @RequestBody CardRequest cardRequest) {
        CardResponse updatedCard = cardService.update(id, cardRequest);
        return new ResponseEntity<>(updatedCard, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID id) {
        cardService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
