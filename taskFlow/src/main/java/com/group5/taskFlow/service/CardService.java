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
    BoardModels board = boardRepository.findById(projectId)
            .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + projectId));

    ColumnsModels column = columnRepository.findById(cardRequest.getColumnId())
            .orElseThrow(() -> new EntityNotFoundException("Column not found with id: " + cardRequest.getColumnId()));

    if (!column.getBoard().getId().equals(projectId)) {
      throw new IllegalArgumentException("Column does not belong to the specified project.");
    }

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

    activityLogService.logActivity(EventType.CARD_CREATED, "Card created: " + savedCard.getTitle(), board.getOwner(), board, savedCard);
    return toCardResponse(savedCard);
  }

  public List<CardResponse> findAllByProjectId(UUID projectId) {
    BoardModels board = boardRepository.findById(projectId)
            .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + projectId));

    return board.getColumns().stream()
            .flatMap(column -> column.getCards().stream())
            .map(this::toCardResponse)
            .collect(Collectors.toList());
  }

  public CardResponse findById(UUID id) {
    CardsModels card = cardRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Card not found with ID: " + id));
    return toCardResponse(card);
  }

  @Transactional
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

    activityLogService.logActivity(EventType.CARD_UPDATED, "Card updated: " + updatedCard.getTitle(), card.getColumn().getBoard().getOwner(), card.getColumn().getBoard(), updatedCard);
    return toCardResponse(updatedCard);
  }

  @Transactional
  public void deleteById(UUID id) {
    CardsModels card = cardRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Card not found with ID: " + id));
    activityLogService.logActivity(EventType.CARD_DELETED, "Card deleted: " + card.getTitle(), card.getColumn().getBoard().getOwner(), card.getColumn().getBoard(), card);
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

  @Transactional
  public void changeStatusWithId(UUID taskId, UUID newColumnId) {
    CardsModels card = cardRepository.findById(taskId)
            .orElseThrow(() -> new NoSuchElementException("Card not found with ID: " + taskId));

    ColumnsModels newColumn = columnRepository.findById(newColumnId)
            .orElseThrow(() -> new EntityNotFoundException("Column not found with id: " + newColumnId));

        card.setColumn(newColumn);

        card.setUpdatedAt(Instant.now());

        cardRepository.save(card);
        activityLogService.logActivity(EventType.CARD_MOVED, "Card " + card.getTitle() + " moved to " + newColumn.getColumnType().getName(), card.getColumn().getBoard().getOwner(), card.getColumn().getBoard(), card);
      }

    

      @Transactional

      public CardResponse assignMeToCard(UUID cardId, UUID userId) {

        CardsModels card = cardRepository.findById(cardId)

                .orElseThrow(() -> new NoSuchElementException("Card not found with ID: " + cardId));

    

        UserModels user = userRepository.findById(userId)

                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

    

        if (!card.getColumn().getBoard().getOwner().getId().equals(userId)) {

          throw new IllegalArgumentException("Only the board owner can assign themselves to a card.");

        }

    

        card.setAssignee(user);

        card.setUpdatedAt(Instant.now());

        CardsModels updatedCard = cardRepository.save(card);

    

        activityLogService.logActivity(EventType.MEMBER_ASSIGNED, "User " + user.getEmail() + " assigned to card " + card.getTitle(), card.getColumn().getBoard().getOwner(), card.getColumn().getBoard(), updatedCard);
        return toCardResponse(updatedCard);

      }

    }
    