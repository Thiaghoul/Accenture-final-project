package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.BoardRequest;
import com.group5.taskFlow.dto.BoardResponse;
import com.group5.taskFlow.dto.CardResponse;
import com.group5.taskFlow.dto.ColumnResponse;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.ColumnsModels;
import com.group5.taskFlow.model.ColumnTypeModels;
import com.group5.taskFlow.repository.BoardRepository;
import com.group5.taskFlow.repository.ColumnRepository;
import com.group5.taskFlow.repository.ColumnTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final ColumnRepository columnRepository;
    private final ColumnTypeRepository columnTypeRepository;
    private final CardService cardService;

    @Autowired
    public BoardService(BoardRepository boardRepository, ColumnRepository columnRepository, ColumnTypeRepository columnTypeRepository, CardService cardService) {
        this.boardRepository = boardRepository;
        this.columnRepository = columnRepository;
        this.columnTypeRepository = columnTypeRepository;
        this.cardService = cardService;
    }

    public BoardResponse save(BoardRequest boardRequest) {
        BoardModels boardModels = new BoardModels();
        boardModels.setName(boardRequest.getName());
        boardModels.setDescription(boardRequest.getDescription());

        BoardModels savedBoard = boardRepository.save(boardModels);

        List<ColumnTypeModels> columnTypes = columnTypeRepository.findAll();
        for (ColumnTypeModels columnType : columnTypes) {
            ColumnsModels column = new ColumnsModels();
            column.setBoard(savedBoard);
            column.setColumnType(columnType);
            column.setOrder(columnType.getOrder());
            columnRepository.save(column);
        }

        return toBoardResponse(savedBoard);
    }

    public List<BoardResponse> findAll() {
        return boardRepository.findAll().stream()
                .map(this::toBoardResponse)
                .collect(Collectors.toList());
    }

    public BoardResponse findById(UUID id) {
        BoardModels board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));
        return toBoardResponse(board);
    }

    public BoardResponse update(UUID id, BoardRequest boardRequest) {
        BoardModels existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));

        existingBoard.setName(boardRequest.getName());
        existingBoard.setDescription(boardRequest.getDescription());

        BoardModels updatedBoard = boardRepository.save(existingBoard);
        return toBoardResponse(updatedBoard);
    }

    public void deleteById(UUID id) {
        if (!boardRepository.existsById(id)) {
            throw new EntityNotFoundException("Board not found with id: " + id);
        }
        boardRepository.deleteById(id);
    }

    private BoardResponse toBoardResponse(BoardModels boardModels) {
        BoardResponse boardResponse = new BoardResponse();
        boardResponse.setId(boardModels.getId());
        boardResponse.setName(boardModels.getName());
        boardResponse.setDescription(boardModels.getDescription());
        boardResponse.setCreatedAt(boardModels.getCreatedAt());
        boardResponse.setUpdatedAt(boardModels.getUpdatedAt());

        List<ColumnResponse> columnResponses = boardModels.getColumns().stream()
                .map(this::toColumnResponse)
                .collect(Collectors.toList());
        boardResponse.setColumns(columnResponses);

        return boardResponse;
    }

    private ColumnResponse toColumnResponse(ColumnsModels columnsModels) {
        ColumnResponse columnResponse = new ColumnResponse();
        columnResponse.setId(columnsModels.getId());
        columnResponse.setName(columnsModels.getColumnType().getName());
        columnResponse.setOrder(columnsModels.getOrder());
        columnResponse.setBoardId(columnsModels.getBoard().getId());

        List<CardResponse> cardResponses = columnsModels.getCards().stream()
                .map(cardService::toCardResponse)
                .collect(Collectors.toList());
        columnResponse.setCards(cardResponses);

        return columnResponse;
    }
}
