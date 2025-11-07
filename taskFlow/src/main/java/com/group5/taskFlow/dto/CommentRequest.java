package com.group5.taskFlow.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.UUID;

@Data
public class CommentRequest {
    @NotEmpty
    private String text;

    private UUID cardId;

    private UUID userId;
}
