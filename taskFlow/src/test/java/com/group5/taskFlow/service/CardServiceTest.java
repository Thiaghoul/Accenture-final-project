package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.CardRequest;
import com.group5.taskFlow.dto.CardResponse;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.CardsModels;
import com.group5.taskFlow.model.ColumnsModels;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.ColumnTypeModels;
import com.group5.taskFlow.model.enums.Priority;
import com.group5.taskFlow.repository.BoardRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private CardService cardService;

    private UUID projectId;
    private CardsModels cardsModels;
    private CardRequest cardRequest;
    private ColumnsModels columnsModels;
    private UserModels userModels;
    private BoardModels boardModels;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();

        boardModels = new BoardModels();
        boardModels.setId(projectId);

        columnsModels = new ColumnsModels();
        columnsModels.setId(columnId);
        columnsModels.setBoard(boardModels);

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

        // Manually inject mocks since @InjectMocks might not handle all constructor parameters automatically after adding new ones
        cardService = new CardService(cardRepository, columnRepository, userRepository, boardRepository, emailService, activityLogService);
    }

    @Test
    public void save_shouldReturnCardResponseAndSendEmail() {
        when(boardRepository.findById(eq(projectId))).thenReturn(Optional.of(boardModels));
        when(columnRepository.findById(any(UUID.class))).thenReturn(Optional.of(columnsModels));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(userModels));
        when(cardRepository.save(any(CardsModels.class))).thenReturn(cardsModels);

        CardResponse result = cardService.save(projectId, cardRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(cardRequest.getTitle());
        assertThat(result.getColumnId()).isEqualTo(cardRequest.getColumnId());
        assertThat(result.getAssigneeId()).isEqualTo(cardRequest.getAssigneeId());
        verify(emailService, times(1)).sendSimpleMessage(eq(userModels.getEmail()), anyString(), anyString());
    }

    @Test
    public void save_whenNoAssignee_shouldNotSendEmail() {
        cardRequest.setAssigneeId(null);
        cardsModels.setAssignee(null);

        when(boardRepository.findById(eq(projectId))).thenReturn(Optional.of(boardModels));
        when(columnRepository.findById(any(UUID.class))).thenReturn(Optional.of(columnsModels));
        when(cardRepository.save(any(CardsModels.class))).thenReturn(cardsModels);

        CardResponse result = cardService.save(projectId, cardRequest);

        assertThat(result).isNotNull();
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    public void save_whenColumnNotFound_shouldThrowEntityNotFoundException() {
        when(boardRepository.findById(eq(projectId))).thenReturn(Optional.of(boardModels));
        when(columnRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cardService.save(projectId, cardRequest));
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    public void save_whenAssigneeNotFound_shouldThrowEntityNotFoundException() {
        when(boardRepository.findById(eq(projectId))).thenReturn(Optional.of(boardModels));
        when(columnRepository.findById(any(UUID.class))).thenReturn(Optional.of(columnsModels));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cardService.save(projectId, cardRequest));
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    public void findAllByProjectId_shouldReturnListOfCardResponses() {
        boardModels.setColumns(Set.of(columnsModels));
        columnsModels.setCards(Set.of(cardsModels));

        when(boardRepository.findById(eq(projectId))).thenReturn(Optional.of(boardModels));

        List<CardResponse> result = cardService.findAllByProjectId(projectId);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(cardsModels.getTitle());
    }

    @Test
    public void update_shouldSendEmailToAssignee() {
        UUID cardId = cardsModels.getId();
        cardRequest.setTitle("Updated Title");

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardsModels));
        when(columnRepository.findById(any(UUID.class))).thenReturn(Optional.of(columnsModels));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(userModels));
        when(cardRepository.save(any(CardsModels.class))).thenReturn(cardsModels);

        CardResponse result = cardService.update(cardId, cardRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        verify(emailService, times(1)).sendSimpleMessage(eq(userModels.getEmail()), anyString(), anyString());
    }

    @Test
    public void update_whenAssigneeRemoved_shouldNotSendEmail() {
        UUID cardId = cardsModels.getId();
        cardRequest.setAssigneeId(null);
        cardsModels.setAssignee(null);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardsModels));
        when(columnRepository.findById(any(UUID.class))).thenReturn(Optional.of(columnsModels));
        when(cardRepository.save(any(CardsModels.class))).thenReturn(cardsModels);

        CardResponse result = cardService.update(cardId, cardRequest);

        assertThat(result).isNotNull();
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    public void changeStatusWithId_shouldUpdateCardColumn() {
        UUID taskId = cardsModels.getId();
        UUID newColumnId = UUID.randomUUID();
        ColumnsModels newColumn = new ColumnsModels();
        newColumn.setId(newColumnId);
        ColumnTypeModels columnType = new ColumnTypeModels();
        columnType.setName("In Progress");
        newColumn.setColumnType(columnType);

        when(cardRepository.findById(eq(taskId))).thenReturn(Optional.of(cardsModels));
        when(columnRepository.findById(eq(newColumnId))).thenReturn(Optional.of(newColumn));
        when(cardRepository.save(any(CardsModels.class))).thenReturn(cardsModels);

        cardService.changeStatusWithId(taskId, newColumnId);

        assertThat(cardsModels.getColumn().getId()).isEqualTo(newColumnId);
        verify(cardRepository, times(1)).save(cardsModels);
    }
}
