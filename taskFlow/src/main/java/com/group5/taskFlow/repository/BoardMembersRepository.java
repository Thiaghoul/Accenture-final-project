package com.group5.taskFlow.repository;

import com.group5.taskFlow.model.BoardMembersModels;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.UserModels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardMembersRepository extends JpaRepository<BoardMembersModels, UUID> {
    boolean existsByBoardAndUser(BoardModels board, UserModels user);
    Optional<BoardMembersModels> findByBoardAndUser(BoardModels board, UserModels user);
}
