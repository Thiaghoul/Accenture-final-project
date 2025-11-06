package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.ColumnRequest;
import com.group5.taskFlow.dto.ColumnResponse;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.ColumnTypeModels;
import com.group5.taskFlow.model.ColumnsModels;
import com.group5.taskFlow.repository.BoardRepository;
import com.group5.taskFlow.repository.ColumnRepository;
import com.group5.taskFlow.repository.ColumnTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ColumnServiceTest {

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private ColumnTypeRepository columnTypeRepository;

    @InjectMocks
    private ColumnService columnService;

    private ColumnsModels columnsModels;
    private ColumnRequest columnRequest;
    private BoardModels boardModels;
    private ColumnTypeModels columnTypeModels;

    @BeforeEach
    void setUp() {
        UUID boardId = UUID.randomUUID();
        UUID columnTypeId = UUID.randomUUID();

        boardModels = new BoardModels();
        boardModels.setId(boardId);
        boardModels.setName("Test Board");

        columnTypeModels = new ColumnTypeModels();
        columnTypeModels.setId(columnTypeId);
        columnTypeModels.setName("To Do");

        columnsModels = new ColumnsModels();
        columnsModels.setId(UUID.randomUUID());
        columnsModels.setOrder(1);
        columnsModels.setBoard(boardModels);
        columnsModels.setColumnType(columnTypeModels);

        columnRequest = new ColumnRequest();
        columnRequest.setOrder(1);
        columnRequest.setBoardId(boardId);
        columnRequest.setColumnTypeId(columnTypeId);
    }

    @Test
    public void save_shouldReturnColumnResponse() {
        when(boardRepository.findById(any(UUID.class))).thenReturn(Optional.of(boardModels));
        when(columnTypeRepository.findById(any(UUID.class))).thenReturn(Optional.of(columnTypeModels));
        when(columnRepository.save(any(ColumnsModels.class))).thenReturn(columnsModels);

        ColumnResponse result = columnService.save(columnRequest);

        assertThat(result).isNotNull();
        assertThat(result.getOrder()).isEqualTo(columnRequest.getOrder());
        assertThat(result.getBoardId()).isEqualTo(columnRequest.getBoardId());
        assertThat(result.getColumnTypeId()).isEqualTo(columnRequest.getColumnTypeId());
    }

    @Test
    public void save_whenBoardNotFound_shouldThrowNoSuchElementException() {
        when(boardRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> columnService.save(columnRequest));
    }

    @Test
    public void save_whenColumnTypeNotFound_shouldThrowNoSuchElementException() {
        when(boardRepository.findById(any(UUID.class))).thenReturn(Optional.of(boardModels));
        when(columnTypeRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> columnService.save(columnRequest));
    }
}
