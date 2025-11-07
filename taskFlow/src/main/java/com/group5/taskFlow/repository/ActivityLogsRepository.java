package com.group5.taskFlow.repository;

import com.group5.taskFlow.model.ActivityLogsModels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityLogsRepository extends JpaRepository<ActivityLogsModels, UUID> {
    List<ActivityLogsModels> findByCardId(UUID cardId);
}
