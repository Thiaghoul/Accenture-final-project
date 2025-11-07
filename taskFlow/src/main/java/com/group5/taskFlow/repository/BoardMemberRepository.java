package com.group5.taskFlow.repository;

import com.group5.taskFlow.model.BoardMembersModels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardMemberRepository extends JpaRepository<BoardMembersModels, UUID> {
    List<BoardMembersModels> findByUserId(UUID userId);
}
