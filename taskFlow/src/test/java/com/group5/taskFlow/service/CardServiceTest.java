package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.CardRequest;
import com.group5.taskFlow.dto.CardResponse;
import com.group5.taskFlow.model.CardsModels;
import com.group5.taskFlow.model.ColumnsModels;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.Priority;
import com.group5.taskFlow.repository.CardRepository;
import com.group5.taskFlow.repository.ColumnRepository;
import com.group5.taskFlow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CardService cardService;

    private CardsModels cardsModels;
    private CardRequest cardRequest;
    private ColumnsModels columnsModels;
    private UserModels userModels;

    @BeforeEach
    void setUp() {
        UUID columnId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();

        columnsModels = new ColumnsModels();
        columnsModels.setId(columnId);

        userModels = new UserModels();
        userModels.setId(assigneeId);
        userModels.setEmail("assignee@example.com");

        cardsModels = new CardsModels();
        cardsModels.setId(UUID.randomUUID());
        cardsModels.setTitle("Test Card");
        cardsModels.setDescription("Description for test card");
        cardsModels.setPriority(Priority.MEDIUM);
        cardsModels.setDueDate(LocalDate.now().plusDays(7));
        cardsModels.setCompletionPercentage(0);
        cardsModels.setColumn(columnsModels);
        cardsModels.setAssignee(userModels);

        cardRequest = new CardRequest();
        cardRequest.setTitle("Test Card");
        cardRequest.setDescription("Description for test card");
        cardRequest.setPriority(Priority.MEDIUM);
        cardRequest.setDueDate(LocalDate.now().plusDays(7));
        cardRequest.setColumnId(columnId);
        cardRequest.setAssigneeId(assigneeId);
    }

    @Test
    public void save_shouldReturnCardResponse() {
        when(columnRepository.findById(any(UUID.class))).thenReturn(Optional.of(columnsModels));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(userModels));
        when(cardRepository.save(any(CardsModels.class))).thenReturn(cardsModels);

        CardResponse result = cardService.save(cardRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(cardRequest.getTitle());
        assertThat(result.getColumnId()).isEqualTo(cardRequest.getColumnId());
        assertThat(result.getAssigneeId()).isEqualTo(cardRequest.getAssigneeId());
    }

    @Test
    public void save_whenColumnNotFound_shouldThrowNoSuchElementException() {
        when(columnRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cardService.save(cardRequest));
    }

    @Test
    public void save_whenAssigneeNotFound_shouldThrowNoSuchElementException() {
        when(columnRepository.findById(any(UUID.class))).thenReturn(Optional.of(columnsModels));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cardService.save(cardRequest));
    }

  @Test
  void getAllCardOfBoard() {

  }
}
