package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.CardRequest;
import com.group5.taskFlow.dto.CardResponse;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.CardsModels;
import com.group5.taskFlow.model.ColumnsModels;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.EventType;
import com.group5.taskFlow.repository.BoardRepository;
import com.group5.taskFlow.repository.CardRepository;
import com.group5.taskFlow.repository.ColumnRepository;
import com.group5.taskFlow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CardService {
    private static final Logger log = LoggerFactory.getLogger(CardService.class);

    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final EmailService emailService;
    private final ActivityLogService activityLogService;

    @Autowired
    public CardService(CardRepository cardRepository, ColumnRepository columnRepository, UserRepository userRepository, BoardRepository boardRepository, EmailService emailService, ActivityLogService activityLogService) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.emailService = emailService;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public CardResponse save(UUID projectId, CardRequest cardRequest) {
        log.info("Attempting to save card in project with id: {}", projectId);
        BoardModels board = boardRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("Board not found with id: {}", projectId);
                    return new EntityNotFoundException("Board not found with id: " + projectId);
                });

        ColumnsModels column = columnRepository.findById(cardRequest.getColumnId())
                .orElseThrow(() -> {
                    log.error("Column not found with id: {}", cardRequest.getColumnId());
                    return new EntityNotFoundException("Column not found with id: " + cardRequest.getColumnId());
                });

        if (!column.getBoard().getId().equals(projectId)) {
            log.error("Column with id: {} does not belong to project with id: {}", column.getId(), projectId);
            throw new IllegalArgumentException("Column does not belong to the specified project.");
        }

        UserModels assignee = null;
        if (cardRequest.getAssigneeId() != null) {
            assignee = userRepository.findById(cardRequest.getAssigneeId())
                    .orElseThrow(() -> {
                        log.error("Assignee not found with id: {}", cardRequest.getAssigneeId());
                        return new EntityNotFoundException("Assignee not found with id: " + cardRequest.getAssigneeId());
                    });
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
        log.info("Card with id: {} saved successfully", savedCard.getId());

        if (assignee != null) {
            emailService.sendSimpleMessage(assignee.getEmail(), "You have been assigned a new task", "You have been assigned the task: " + savedCard.getTitle());
        }

        activityLogService.logActivity(EventType.CARD_CREATED, "Card created: " + savedCard.getTitle(), board.getOwner(), board, savedCard);
        return toCardResponse(savedCard);
    }

    public List<CardResponse> findAllByProjectId(UUID projectId) {
        log.info("Fetching all cards for project with id: {}", projectId);
        BoardModels board = boardRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("Board not found with id: {}", projectId);
                    return new EntityNotFoundException("Board not found with id: " + projectId);
                });

        List<CardResponse> cards = board.getColumns().stream()
                .flatMap(column -> column.getCards().stream())
                .map(this::toCardResponse)
                .collect(Collectors.toList());
        log.info("Found {} cards for project with id: {}", cards.size(), projectId);
        return cards;
    }

    public CardResponse findById(UUID id) {
        log.info("Fetching card with id: {}", id);
        CardsModels card = cardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Card not found with id: {}", id);
                    return new NoSuchElementException("Card not found with ID: " + id);
                });
        return toCardResponse(card);
    }

    @Transactional
    public CardResponse update(UUID id, CardRequest cardRequest) {
        log.info("Updating card with id: {}", id);
        CardsModels card = cardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Card not found with id: {}", id);
                    return new NoSuchElementException("Card not found with ID: " + id);
                });

        ColumnsModels column = columnRepository.findById(cardRequest.getColumnId())
                .orElseThrow(() -> {
                    log.error("Column not found with id: {}", cardRequest.getColumnId());
                    return new EntityNotFoundException("Column not found with id: " + cardRequest.getColumnId());
                });

        UserModels assignee = null;
        if (cardRequest.getAssigneeId() != null) {
            assignee = userRepository.findById(cardRequest.getAssigneeId())
                    .orElseThrow(() -> {
                        log.error("Assignee not found with id: {}", cardRequest.getAssigneeId());
                        return new EntityNotFoundException("Assignee not found with id: " + cardRequest.getAssigneeId());
                    });
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
        log.info("Card with id: {} updated successfully", id);

        if (assignee != null) {
            emailService.sendSimpleMessage(assignee.getEmail(), "A task assigned to you has been updated", "The task: " + updatedCard.getTitle() + " has been updated.");
        }

        activityLogService.logActivity(EventType.CARD_UPDATED, "Card updated: " + updatedCard.getTitle(), card.getColumn().getBoard().getOwner(), card.getColumn().getBoard(), updatedCard);
        return toCardResponse(updatedCard);
    }

    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting card with id: {}", id);
        CardsModels card = cardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Card not found with id: {}", id);
                    return new NoSuchElementException("Card not found with ID: " + id);
                });
        activityLogService.logActivity(EventType.CARD_DELETED, "Card deleted: " + card.getTitle(), card.getColumn().getBoard().getOwner(), card.getColumn().getBoard(), card);
        cardRepository.deleteById(id);
        log.info("Card with id: {} deleted successfully", id);
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

    @Transactional
    public void changeStatusWithId(UUID taskId, UUID newColumnId) {
        log.info("Changing status of task with id: {} to column with id: {}", taskId, newColumnId);
        CardsModels card = cardRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Card not found with id: {}", taskId);
                    return new NoSuchElementException("Card not found with ID: " + taskId);
                });

        ColumnsModels newColumn = columnRepository.findById(newColumnId)
                .orElseThrow(() -> {
                    log.error("Column not found with id: {}", newColumnId);
                    return new EntityNotFoundException("Column not found with id: " + newColumnId);
                });

        card.setColumn(newColumn);

        card.setUpdatedAt(Instant.now());

        cardRepository.save(card);
        log.info("Status of task with id: {} changed successfully", taskId);
        activityLogService.logActivity(EventType.CARD_MOVED, "Card " + card.getTitle() + " moved to " + newColumn.getColumnType().getName(), card.getColumn().getBoard().getOwner(), card.getColumn().getBoard(), card);
    }

    @Transactional
    public CardResponse assignMeToCard(UUID cardId, UUID userId) {
        log.info("Assigning user with id: {} to card with id: {}", userId, cardId);
        CardsModels card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.error("Card not found with id: {}", cardId);
                    return new NoSuchElementException("Card not found with ID: " + cardId);
                });

        UserModels user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new EntityNotFoundException("User not found with id: " + userId);
                });

        if (!card.getColumn().getBoard().getOwner().getId().equals(userId)) {
            log.error("User with id: {} is not the owner of the board and cannot assign themselves to a card", userId);
            throw new IllegalArgumentException("Only the board owner can assign themselves to a card.");
        }

        card.setAssignee(user);
        card.setUpdatedAt(Instant.now());
        CardsModels updatedCard = cardRepository.save(card);
        log.info("User with id: {} assigned to card with id: {} successfully", userId, cardId);

        activityLogService.logActivity(EventType.MEMBER_ASSIGNED, "User " + user.getEmail() + " assigned to card " + card.getTitle(), card.getColumn().getBoard().getOwner(), card.getColumn().getBoard(), updatedCard);
        return toCardResponse(updatedCard);
    }
}

    