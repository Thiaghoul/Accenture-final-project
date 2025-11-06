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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;
    private final UserRepository userRepository;

    @Autowired
    public CardService(CardRepository cardRepository, ColumnRepository columnRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.userRepository = userRepository;
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

        CardsModels savedCard = cardRepository.save(card);
        return toCardResponse(savedCard);
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
}