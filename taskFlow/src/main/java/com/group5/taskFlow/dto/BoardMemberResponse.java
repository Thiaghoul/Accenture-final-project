package com.group5.taskFlow.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class BoardMemberResponse {
    private UUID id;
    private UUID userId;
    private UUID boardId;
    private String memberRole;
}
