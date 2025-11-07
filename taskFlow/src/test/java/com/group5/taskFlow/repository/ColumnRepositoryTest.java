package com.group5.taskFlow.repository;

import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.ColumnTypeModels;
import com.group5.taskFlow.model.ColumnsModels;
import com.group5.taskFlow.model.UserModels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ColumnRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ColumnRepository columnRepository;

    private BoardModels board;
    private ColumnTypeModels columnType;
    private UserModels boardOwner;

    @BeforeEach
    void setUp() {
        boardOwner = new UserModels();
        boardOwner.setEmail("boardowner@example.com");
        boardOwner.setPasswordHash("password");
        boardOwner.setFirstName("Board");
        boardOwner.setLastName("Owner");
        entityManager.persist(boardOwner);

        board = new BoardModels();
        board.setName("Test Board");
        board.setCreatedAt(Instant.now());
        board.setUpdatedAt(Instant.now());
        board.setOwner(boardOwner);
        entityManager.persist(board);

        columnType = new ColumnTypeModels();
        columnType.setName("To Do");
        columnType.setOrder(1);
        entityManager.persist(columnType);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should save a column successfully")
    void saveColumn_Success() {
        ColumnsModels newColumn = new ColumnsModels();
        newColumn.setBoard(board);
        newColumn.setColumnType(columnType);
        newColumn.setOrder(1);

        ColumnsModels savedColumn = columnRepository.save(newColumn);

        assertThat(savedColumn).isNotNull();
        assertThat(savedColumn.getId()).isNotNull();
        assertThat(savedColumn.getBoard()).isEqualTo(board);
        assertThat(savedColumn.getColumnType()).isEqualTo(columnType);
    }

    @Test
    @DisplayName("Should find columns by board id")
    void findByBoardId_WhenColumnsExist_ReturnsColumnList() {
        // given
        ColumnsModels column1 = new ColumnsModels();
        column1.setBoard(board);
        column1.setColumnType(columnType);
        column1.setOrder(1);
        entityManager.persist(column1);

        ColumnsModels column2 = new ColumnsModels();
        column2.setBoard(board);
        column2.setColumnType(columnType);
        column2.setOrder(2);
        entityManager.persist(column2);

        entityManager.flush();

        // when
        List<ColumnsModels> foundColumns = columnRepository.findByBoardId(board.getId());

        // then
        assertThat(foundColumns).hasSize(2);
        assertThat(foundColumns).contains(column1, column2);
    }

    @Test
    @DisplayName("Should return empty list when no columns exist for a board id")
    void findByBoardId_WhenNoColumnsExist_ReturnsEmptyList() {
        // given
        BoardModels anotherBoard = new BoardModels();
        anotherBoard.setName("Another Board");
        anotherBoard.setCreatedAt(Instant.now());
        anotherBoard.setUpdatedAt(Instant.now());
        anotherBoard.setOwner(boardOwner);
        entityManager.persistAndFlush(anotherBoard);

        // when
        List<ColumnsModels> foundColumns = columnRepository.findByBoardId(anotherBoard.getId());

        // then
        assertThat(foundColumns).isEmpty();
    }
}