package com.group5.taskFlow.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CommentResponse {
    private UUID id;
    private String content;
    private UUID cardId;
    private UUID userId;
    private Instant createdAt;

    public void setCardId(UUID cardId) {
        this.cardId = cardId;
    }
}
