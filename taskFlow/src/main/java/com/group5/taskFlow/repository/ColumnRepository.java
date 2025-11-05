package com.group5.taskFlow.repository;

import com.group5.taskFlow.model.ColumnsModels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ColumnRepository extends JpaRepository<ColumnsModels, UUID> {
}
