package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.ColumnRequest;
import com.group5.taskFlow.dto.ColumnResponse;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.ColumnTypeModels;
import com.group5.taskFlow.model.ColumnsModels;
import com.group5.taskFlow.repository.BoardRepository;
import com.group5.taskFlow.repository.ColumnRepository;
import com.group5.taskFlow.repository.ColumnTypeRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ColumnService {

    private final ColumnRepository columnRepository;
    private final BoardRepository boardRepository;
    private final ColumnTypeRepository columnTypeRepository;

    public ColumnService(ColumnRepository columnRepository, BoardRepository boardRepository, ColumnTypeRepository columnTypeRepository) {
        this.columnRepository = columnRepository;
        this.boardRepository = boardRepository;
        this.columnTypeRepository = columnTypeRepository;
    }

    public ColumnResponse save(ColumnRequest columnRequest) {
        ColumnsModels columnsModels = new ColumnsModels();
        columnsModels.setOrder(columnRequest.getOrder());

        if (columnRequest.getBoardId() != null) {
            BoardModels board = findBoardById(columnRequest.getBoardId());
            columnsModels.setBoard(board);
        }

        if (columnRequest.getColumnTypeId() != null) {
            ColumnTypeModels columnType = findColumnTypeById(columnRequest.getColumnTypeId());
            columnsModels.setColumnType(columnType);
        }

        ColumnsModels savedColumn = columnRepository.save(columnsModels);

        return toColumnResponse(savedColumn);
    }

    private BoardModels findBoardById(UUID boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("Board not found with ID: " + boardId));
    }

    private ColumnTypeModels findColumnTypeById(UUID columnTypeId) {
        return columnTypeRepository.findById(columnTypeId)
                .orElseThrow(() -> new NoSuchElementException("Column Type not found with ID: " + columnTypeId));
    }

    private ColumnResponse toColumnResponse(ColumnsModels columnsModels) {
        ColumnResponse columnResponse = new ColumnResponse();
        columnResponse.setId(columnsModels.getId());
        columnResponse.setOrder(columnsModels.getOrder());
        if (columnsModels.getBoard() != null) {
            columnResponse.setBoardId(columnsModels.getBoard().getId());
        }
        if (columnsModels.getColumnType() != null) {
            columnResponse.setColumnTypeId(columnsModels.getColumnType().getId());
        }
        return columnResponse;
    }
}
