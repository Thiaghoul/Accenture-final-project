package com.group5.taskFlow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BoardRequest {
    @NotBlank
    private String name;
    private String description;
}
