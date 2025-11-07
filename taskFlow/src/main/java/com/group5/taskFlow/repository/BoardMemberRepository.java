package com.group5.taskFlow.repository;

import com.group5.taskFlow.model.BoardMembersModels;
import com.group5.taskFlow.model.pk.BoardMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardMemberRepository extends JpaRepository<BoardMembersModels, BoardMemberId> {
}
