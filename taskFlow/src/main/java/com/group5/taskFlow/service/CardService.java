package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.CardRequest;
import com.group5.taskFlow.dto.CardResponse;
import com.group5.taskFlow.model.CardsModels;
import com.group5.taskFlow.model.ColumnsModels;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.repository.CardRepository;
import com.group5.taskFlow.repository.ColumnRepository;
import com.group5.taskFlow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;
    private final UserRepository userRepository;

    public CardService(CardRepository cardRepository, ColumnRepository columnRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.userRepository = userRepository;
    }

    public CardResponse save(CardRequest cardRequest) {
        CardsModels cardsModels = new CardsModels();
        cardsModels.setTitle(cardRequest.getTitle());
        cardsModels.setDescription(cardRequest.getDescription());
        cardsModels.setPriority(cardRequest.getPriority());
        cardsModels.setDueDate(cardRequest.getDueDate());

        if (cardRequest.getColumnId() != null) {
            ColumnsModels column = findColumnById(cardRequest.getColumnId());
            cardsModels.setColumn(column);
        }

        if (cardRequest.getAssigneeId() != null) {
            UserModels assignee = findUserById(cardRequest.getAssigneeId());
            cardsModels.setAssignee(assignee);
        }

        CardsModels savedCard = cardRepository.save(cardsModels);

        return toCardResponse(savedCard);
    }

    public List<CardResponse> findAll() {
        return cardRepository.findAll().stream()
                .map(this::toCardResponse)
                .collect(Collectors.toList());
    }

    public CardResponse findById(UUID id) {
        CardsModels card = cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Card not found with id: " + id));
        return toCardResponse(card);
    }

    public CardResponse update(UUID id, CardRequest cardRequest) {
        CardsModels existingCard = cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Card not found with id: " + id));

        existingCard.setTitle(cardRequest.getTitle());
        existingCard.setDescription(cardRequest.getDescription());
        existingCard.setPriority(cardRequest.getPriority());
        existingCard.setDueDate(cardRequest.getDueDate());

        if (cardRequest.getColumnId() != null) {
            ColumnsModels column = findColumnById(cardRequest.getColumnId());
            existingCard.setColumn(column);
        }

        if (cardRequest.getAssigneeId() != null) {
            UserModels assignee = findUserById(cardRequest.getAssigneeId());
            existingCard.setAssignee(assignee);
        }

        CardsModels updatedCard = cardRepository.save(existingCard);
        return toCardResponse(updatedCard);
    }

    public void deleteById(UUID id) {
        if (!cardRepository.existsById(id)) {
            throw new EntityNotFoundException("Card not found with id: " + id);
        }
        cardRepository.deleteById(id);
    }

    private ColumnsModels findColumnById(UUID columnId) {
        return columnRepository.findById(columnId)
                .orElseThrow(() -> new NoSuchElementException("Column not found with ID: " + columnId));
    }

    private UserModels findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));
    }

    private CardResponse toCardResponse(CardsModels cardsModels) {
        CardResponse cardResponse = new CardResponse();
        cardResponse.setId(cardsModels.getId());
        cardResponse.setTitle(cardsModels.getTitle());
        cardResponse.setDescription(cardsModels.getDescription());
        cardResponse.setPriority(cardsModels.getPriority());
        cardResponse.setDueDate(cardsModels.getDueDate());
        cardResponse.setCompletionPercentage(cardsModels.getCompletionPercentage());
        cardResponse.setCreatedAt(cardsModels.getCreatedAt());
        cardResponse.setUpdatedAt(cardsModels.getUpdatedAt());
        if (cardsModels.getColumn() != null) {
            cardResponse.setColumnId(cardsModels.getColumn().getId());
        }
        if (cardsModels.getAssignee() != null) {
            cardResponse.setAssigneeId(cardsModels.getAssignee().getId());
        }
        return cardResponse;
    }
}
