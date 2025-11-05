package com.group5.taskFlow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ColumnTypeRequest {
    @NotBlank
    private String name;

    @NotNull
    private Integer order;
}
