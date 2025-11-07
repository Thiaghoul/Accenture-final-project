package com.group5.taskFlow.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CommentResponse {
    private UUID id;
    private String content;
    private UUID userId;
    private String userName;
    private Instant createdAt;
}
