package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.BoardRequest;
import com.group5.taskFlow.dto.BoardResponse;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {

        this.boardRepository = boardRepository;
    }

    public BoardResponse save(BoardRequest boardRequest) {
        BoardModels boardModels = new BoardModels();
        boardModels.setName(boardRequest.getName());
        boardModels.setDescription(boardRequest.getDescription());

        BoardModels savedBoard = boardRepository.save(boardModels);

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
        return boardResponse;
    }
}
