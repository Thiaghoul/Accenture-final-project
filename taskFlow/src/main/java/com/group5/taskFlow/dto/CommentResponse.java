package com.group5.taskFlow.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CommentResponse {
    private UUID id;
    private String text;
    private Instant createdAt;
    private UUID cardId;
    private UUID userId;
}
