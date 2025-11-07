package com.group5.taskFlow.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class BoardResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID ownerId;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ColumnResponse> columns;
}
