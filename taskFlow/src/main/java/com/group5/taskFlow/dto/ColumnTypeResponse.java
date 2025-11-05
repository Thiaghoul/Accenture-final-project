package com.group5.taskFlow.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ColumnTypeResponse {
    private UUID id;
    private String name;
    private Integer order;
}
