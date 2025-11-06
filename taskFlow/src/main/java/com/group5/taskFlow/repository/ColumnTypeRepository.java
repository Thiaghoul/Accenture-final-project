package com.group5.taskFlow.repository;

import com.group5.taskFlow.model.ColumnTypeModels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ColumnTypeRepository extends JpaRepository<ColumnTypeModels, UUID> {

    Optional<ColumnTypeModels> findByName(String name);
}
