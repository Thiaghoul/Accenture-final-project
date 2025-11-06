package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.BoardResponse;
import com.group5.taskFlow.dto.CardRequest;
import com.group5.taskFlow.dto.CardResponse;
import com.group5.taskFlow.dto.ColumnResponse;
import com.group5.taskFlow.model.BoardModels;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CardService {

  private final CardRepository cardRepository;
  private final ColumnRepository columnRepository;
  private final UserRepository userRepository;
//  private final BoardService boardService;
  private final BoardRepository boardRepository;

  @Autowired
  public CardService(CardRepository cardRepository, ColumnRepository columnRepository, UserRepository userRepository, BoardRepository boardRepository) {
    this.cardRepository = cardRepository;
    this.columnRepository = columnRepository;
    this.userRepository = userRepository;
    this.boardRepository = boardRepository;
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

  /* Problema encontrado:
  Nao esta resgando corretamente as colunas de acordo com o board.
  Uma abordagem viavel seria resgatar as colunas de acordo com o id da board.
   */
  public List<CardResponse> getAllCardOfBoard(UUID id) {
    System.out.println("getAllCardOfBoard - STARTED");
    BoardModels resultSearch = boardRepository.findById(id).get();
    Set<ColumnsModels> columns = resultSearch.getColumns();
    List<CardsModels> cards = new ArrayList<>();
    System.out.println("getAllCardOfBoard - SEARCHING");
    for (ColumnsModels column : columns) {
      cards.addAll(column.getCards());
    }

    System.out.println("getAllCardOfBoard - GET ALL CARDS AND FINISHED");

    var resultsCards = cards.stream().map(this::toCardResponse).toList();

    System.out.println("getAllCardOfBoard - GET ALL CARDS RESPONSE TO USER");
    return resultsCards;
  }

  public void changeStatusWithId(UUID id) {
    var result = cardRepository.findById(id);
    CardsModels card = result.get();

    card.setCompletionPercentage(1);
    cardRepository.save(card);
  }
}