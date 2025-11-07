package com.group5.taskFlow.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CommentRequest {
    private UUID userId;
    private UUID cardId;
    private String content;
}
