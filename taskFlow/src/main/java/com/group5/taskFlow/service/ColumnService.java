package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.CardResponse;
import com.group5.taskFlow.dto.ColumnRequest;
import com.group5.taskFlow.dto.ColumnResponse;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.ColumnTypeModels;
import com.group5.taskFlow.model.ColumnsModels;
import com.group5.taskFlow.repository.BoardRepository;
import com.group5.taskFlow.repository.ColumnRepository;
import com.group5.taskFlow.repository.ColumnTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public List<ColumnResponse> findAll() {
        return columnRepository.findAll().stream()
                .map(this::toColumnResponse)
                .collect(Collectors.toList());
    }

    public ColumnResponse findById(UUID id) {
        ColumnsModels columnsModels = columnRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Column not found with ID: " + id));
        return toColumnResponse(columnsModels);
    }

    public ColumnResponse update(UUID id, ColumnRequest columnRequest) {
        ColumnsModels columnsModels = columnRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Column not found with ID: " + id));

        columnsModels.setOrder(columnRequest.getOrder());

        if (columnRequest.getBoardId() != null) {
            BoardModels board = findBoardById(columnRequest.getBoardId());
            columnsModels.setBoard(board);
        }

        if (columnRequest.getColumnTypeId() != null) {
            ColumnTypeModels columnType = findColumnTypeById(columnRequest.getColumnTypeId());
            columnsModels.setColumnType(columnType);
        }

        ColumnsModels updatedColumn = columnRepository.save(columnsModels);

        return toColumnResponse(updatedColumn);
    }

    public void deleteById(UUID id) {
        columnRepository.deleteById(id);
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
            columnResponse.setName(columnsModels.getColumnType().getName());
        }

        if(columnsModels.getCards() != null && !columnsModels.getCards().isEmpty()){
            columnResponse.setCards(
                    columnsModels.getCards().stream()
                            .map(card -> {
                                var cardResponse = new CardResponse();
                                cardResponse.setId(card.getId());
                                cardResponse.setTitle(card.getTitle());
                                cardResponse.setDescription(card.getDescription());
                                cardResponse.setPriority(card.getPriority());
                                cardResponse.setDueDate(card.getDueDate());
                                cardResponse.setCompletionPercentage(card.getCompletionPercentage());
                                cardResponse.setColumnId(card.getColumn().getId());
                                cardResponse.setAssigneeId(card.getAssignee().getId());
                                cardResponse.setCreatedAt(card.getCreatedAt());
                                cardResponse.setUpdatedAt(card.getUpdatedAt());
                                return cardResponse;
                            })
                            .collect(Collectors.toList())
            );
        }
        return columnResponse;
    }
}
