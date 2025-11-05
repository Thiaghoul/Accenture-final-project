package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.BoardRequest;
import com.group5.taskFlow.dto.BoardResponse;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.repository.BoardRepository;
import org.springframework.stereotype.Service;

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
