package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.CardRequest;
import com.group5.taskFlow.dto.CardResponse;
import com.group5.taskFlow.model.CardsModels;
import com.group5.taskFlow.model.ColumnsModels;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.repository.BoardRepository;
import com.group5.taskFlow.repository.CardRepository;
import com.group5.taskFlow.repository.ColumnRepository;
import com.group5.taskFlow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final EmailService emailService;

    @Autowired
    public CardService(CardRepository cardRepository, ColumnRepository columnRepository, UserRepository userRepository, BoardRepository boardRepository, EmailService emailService) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.emailService = emailService;
    }

    public CardResponse save(CardRequest cardRequest) {
        ColumnsModels column = columnRepository.findById(cardRequest.getColumnId())
                .orElseThrow(() -> new EntityNotFoundException("Column not found with id: " + cardRequest.getColumnId()));

        UserModels assignee = null;
        if (cardRequest.getAssigneeId() != null) {
            assignee = userRepository.findById(cardRequest.getAssigneeId())
                    .orElseThrow(() -> new EntityNotFoundException("Assignee not found with id: " + cardRequest.getAssigneeId()));
        }

        CardsModels card = new CardsModels();
        card.setTitle(cardRequest.getTitle());
        card.setDescription(cardRequest.getDescription());
        card.setPriority(cardRequest.getPriority());
        card.setDueDate(cardRequest.getDueDate());
        card.setColumn(column);
        card.setAssignee(assignee);
        card.setCreatedAt(Instant.now());
        card.setUpdatedAt(Instant.now());

        CardsModels savedCard = cardRepository.save(card);

        if (assignee != null) {
            emailService.sendSimpleMessage(assignee.getEmail(), "You have been assigned a new task", "You have been assigned the task: " + savedCard.getTitle());
        }

        return toCardResponse(savedCard);
    }

    public List<CardResponse> findAll() {
        return cardRepository.findAll().stream()
                .map(this::toCardResponse)
                .collect(Collectors.toList());
    }

    public CardResponse findById(UUID id) {
        CardsModels card = cardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Card not found with ID: " + id));
        return toCardResponse(card);
    }

    public CardResponse update(UUID id, CardRequest cardRequest) {
        CardsModels card = cardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Card not found with ID: " + id));

        ColumnsModels column = columnRepository.findById(cardRequest.getColumnId())
                .orElseThrow(() -> new EntityNotFoundException("Column not found with id: " + cardRequest.getColumnId()));

        UserModels assignee = null;
        if (cardRequest.getAssigneeId() != null) {
            assignee = userRepository.findById(cardRequest.getAssigneeId())
                    .orElseThrow(() -> new EntityNotFoundException("Assignee not found with id: " + cardRequest.getAssigneeId()));
        }

        card.setTitle(cardRequest.getTitle());
        card.setDescription(cardRequest.getDescription());
        card.setPriority(cardRequest.getPriority());
        card.setDueDate(cardRequest.getDueDate());
        card.setColumn(column);
        card.setAssignee(assignee);
        card.setCompletionPercentage(cardRequest.getCompletionPercentage());
        card.setUpdatedAt(Instant.now());

        CardsModels updatedCard = cardRepository.save(card);

        if (assignee != null) {
            emailService.sendSimpleMessage(assignee.getEmail(), "A task assigned to you has been updated", "The task: " + updatedCard.getTitle() + " has been updated.");
        }

        return toCardResponse(updatedCard);
    }

    public void deleteById(UUID id) {
        cardRepository.deleteById(id);
    }

    public CardResponse toCardResponse(CardsModels card) {
        CardResponse cardResponse = new CardResponse();
        cardResponse.setId(card.getId());
        cardResponse.setTitle(card.getTitle());
        cardResponse.setDescription(card.getDescription());
        cardResponse.setPriority(card.getPriority());
        cardResponse.setDueDate(card.getDueDate());
        cardResponse.setCompletionPercentage(card.getCompletionPercentage());
        cardResponse.setCreatedAt(card.getCreatedAt());
        cardResponse.setUpdatedAt(card.getUpdatedAt());
        cardResponse.setColumnId(card.getColumn().getId());
        if (card.getAssignee() != null) {
            cardResponse.setAssigneeId(card.getAssignee().getId());
        }
        return cardResponse;
    }

    public List<CardResponse> getAllCardOfBoard(UUID id) {
        return cardRepository.findByBoardId(id).stream()
                .map(this::toCardResponse)
                .collect(Collectors.toList());
    }

    public void changeStatusWithId(UUID id) {
        var result = cardRepository.findById(id);
        CardsModels card = result.get();

        card.setCompletionPercentage(1);
        cardRepository.save(card);
    }
}