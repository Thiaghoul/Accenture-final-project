package com.group5.taskFlow.repository;

import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.UserModels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardRepository extends JpaRepository<BoardModels, UUID> {

    Optional<BoardModels> findByName(String name);
    List<BoardModels> findByOwnerId(UUID ownerId);
    List<BoardModels> findByMembers_User_Id(UUID userId);
    List<BoardModels> findByOwner(UserModels owner);
}
