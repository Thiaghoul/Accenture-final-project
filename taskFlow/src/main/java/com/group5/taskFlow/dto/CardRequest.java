package com.group5.taskFlow.dto;

import com.group5.taskFlow.model.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CardRequest {
    @NotBlank
    private String title;
    private String description;

    @NotNull
    private Priority priority;

    private LocalDate dueDate;

    private UUID columnId;
    private UUID assigneeId;
    private Integer completionPercentage;

}
