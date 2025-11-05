package com.group5.taskFlow.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ColumnResponse {
    private UUID id;
    private UUID boardId;
    private UUID columnTypeId;
    private Integer order;
}
