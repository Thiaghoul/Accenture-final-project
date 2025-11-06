package com.group5.taskFlow.repository;

import com.group5.taskFlow.model.*;
import com.group5.taskFlow.model.enums.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CardRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CardRepository cardRepository;

    private ColumnsModels column;
    private UserModels assignee;

    @BeforeEach
    void setUp() {
        // Setup entities that Card depends on
        BoardModels board = new BoardModels();
        board.setName("Test Board");
        board.setCreatedAt(Instant.now());
        board.setUpdatedAt(Instant.now());
        entityManager.persist(board);

        ColumnTypeModels columnType = new ColumnTypeModels();
        columnType.setName("To Do");
        columnType.setOrder(1);
        entityManager.persist(columnType);

        column = new ColumnsModels();
        column.setBoard(board);
        column.setColumnType(columnType);
        column.setOrder(1);
        entityManager.persist(column);

        assignee = new UserModels();
        assignee.setEmail("assignee@example.com");
        assignee.setPasswordHash("password");
        assignee.setFirstName("Assignee");
        assignee.setLastName("User");
        entityManager.persist(assignee);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should save a card successfully with all relationships")
    void saveCard_Success() {
        // given
        CardsModels newCard = new CardsModels();
        newCard.setTitle("My First Card");
        newCard.setDescription("This is a test card.");
        newCard.setPriority(Priority.HIGH);
        newCard.setDueDate(LocalDate.now().plusDays(5));
        newCard.setCompletionPercentage(0); // Explicitly setting default
        newCard.setCreatedAt(Instant.now()); // Explicitly setting default
        newCard.setUpdatedAt(Instant.now()); // Explicitly setting default
        newCard.setColumn(column);
        newCard.setAssignee(assignee);

        // when
        // Save the card directly using its repository
        CardsModels savedCard = cardRepository.saveAndFlush(newCard);

        // then
        assertThat(savedCard).isNotNull();
        assertThat(savedCard.getId()).isNotNull();
        assertThat(savedCard.getTitle()).isEqualTo("My First Card");
        assertThat(savedCard.getColumn()).isEqualTo(column);
        assertThat(savedCard.getAssignee()).isEqualTo(assignee);
        assertThat(savedCard.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    @DisplayName("Should save a card with a null assignee")
    void saveCard_WithNullAssignee_Success() {
        // given
        CardsModels newCard = new CardsModels();
        newCard.setTitle("Unassigned Card");
        newCard.setColumn(column);
        newCard.setAssignee(null); // Explicitly set assignee to null
        newCard.setPriority(Priority.LOW); // Set priority as it's non-nullable
        newCard.setCompletionPercentage(0);
        newCard.setCreatedAt(Instant.now());
        newCard.setUpdatedAt(Instant.now());

        // when
        CardsModels savedCard = cardRepository.saveAndFlush(newCard);

        // then
        assertThat(savedCard).isNotNull();
        assertThat(savedCard.getId()).isNotNull();
        assertThat(savedCard.getAssignee()).isNull();
        assertThat(savedCard.getPriority()).isEqualTo(Priority.LOW);
    }
}