package com.group5.taskFlow.repository;

import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.UserModels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BoardRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BoardRepository boardRepository;

    private UserModels owner;

    @BeforeEach
    void setUp() {
        owner = new UserModels();
        owner.setEmail("test@example.com");
        owner.setPasswordHash("password"); 
        owner.setFirstName("Test");
        owner.setLastName("User");
        entityManager.persistAndFlush(owner);
    }

    @Test
    @DisplayName("Should save a board successfully")
    void saveBoard_Success() {
        BoardModels newBoard = new BoardModels();
        newBoard.setName("My Test Board");
        newBoard.setDescription("A description for the test board.");
        newBoard.setCreatedAt(Instant.now());
        newBoard.setUpdatedAt(Instant.now());
        newBoard.setOwner(owner);

        BoardModels savedBoard = boardRepository.save(newBoard);

        assertThat(savedBoard).isNotNull();
        assertThat(savedBoard.getId()).isNotNull();
        assertThat(savedBoard.getName()).isEqualTo("My Test Board");
        assertThat(savedBoard.getOwner()).isEqualTo(owner);
    }

    @Test
    @DisplayName("Should find a board by name")
    void findByName_WhenBoardExists_ReturnsBoard() {
        // given
        BoardModels board = new BoardModels();
        board.setName("Findable Board");
        board.setCreatedAt(Instant.now());
        board.setUpdatedAt(Instant.now());
        board.setOwner(owner);
        entityManager.persistAndFlush(board);

        // when
        Optional<BoardModels> found = boardRepository.findByName("Findable Board");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Findable Board");
        assertThat(found.get().getOwner()).isEqualTo(owner);
    }

    @Test
    @DisplayName("Should return empty optional when board with name does not exist")
    void findByName_WhenBoardDoesNotExist_ReturnsEmpty() {
        // when
        Optional<BoardModels> found = boardRepository.findByName("NonExistent Board");

        // then
        assertThat(found).isNotPresent();
    }
}