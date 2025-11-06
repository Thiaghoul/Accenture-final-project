package com.group5.taskFlow.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ColumnResponse {
  private UUID id;
  private UUID boardId;
  private UUID ColumnTypeId;
  private String name;
  private Integer order;
  private List<CardResponse> cards;

}