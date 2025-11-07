package com.group5.taskFlow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ColumnRequest {
    @NotNull
    private UUID boardId;

    @NotNull
    private UUID columnTypeId;

    @NotNull
    private Integer order;
}
