package com.group5.taskFlow.repository;

import com.group5.taskFlow.model.UserModels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModels, UUID> {
    Optional<UserModels> findByEmail(String email);
}
