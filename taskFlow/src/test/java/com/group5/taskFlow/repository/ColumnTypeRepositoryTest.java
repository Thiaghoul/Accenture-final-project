package com.group5.taskFlow.repository;

import com.group5.taskFlow.model.ColumnTypeModels;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ColumnTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ColumnTypeRepository columnTypeRepository;

    @Test
    @DisplayName("Should save a column type successfully")
    void saveColumnType_Success() {
        ColumnTypeModels newColumnType = new ColumnTypeModels();
        newColumnType.setName("To Do");
        newColumnType.setOrder(1);

        ColumnTypeModels savedColumnType = columnTypeRepository.save(newColumnType);

        assertThat(savedColumnType).isNotNull();
        assertThat(savedColumnType.getId()).isNotNull();
        assertThat(savedColumnType.getName()).isEqualTo("To Do");
    }

    @Test
    @DisplayName("Should find a column type by name")
    void findByName_WhenColumnTypeExists_ReturnsColumnType() {
        // given
        ColumnTypeModels columnType = new ColumnTypeModels();
        columnType.setName("In Progress");
        columnType.setOrder(2);
        entityManager.persistAndFlush(columnType);

        // when
        Optional<ColumnTypeModels> found = columnTypeRepository.findByName("In Progress");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("In Progress");
    }

    @Test
    @DisplayName("Should return empty optional when column type with name does not exist")
    void findByName_WhenColumnTypeDoesNotExist_ReturnsEmpty() {
        // when
        Optional<ColumnTypeModels> found = columnTypeRepository.findByName("NonExistent Type");

        // then
        assertThat(found).isNotPresent();
    }
}