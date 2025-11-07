package com.group5.taskFlow.repository;

import com.group5.taskFlow.model.CommentsModels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentsModels, UUID> {
}
