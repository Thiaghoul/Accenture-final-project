package com.group5.taskFlow.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long userId;
    private Long cardId;
}
