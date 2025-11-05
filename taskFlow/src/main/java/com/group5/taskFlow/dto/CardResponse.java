package com.group5.taskFlow.dto;

import com.group5.taskFlow.model.enums.Priority;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CardResponse {
    private UUID id;
    private String title;
    private String description;
    private Priority priority;
    private LocalDate dueDate;
    private Integer completionPercentage;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID columnId;
    private UUID assigneeId;
}
