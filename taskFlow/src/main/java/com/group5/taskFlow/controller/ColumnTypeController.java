package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.ColumnTypeRequest;
import com.group5.taskFlow.dto.ColumnTypeResponse;
import com.group5.taskFlow.service.ColumnTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/columns/types")
public class ColumnTypeController {

  private final ColumnTypeService columnTypeService;

  public ColumnTypeController(ColumnTypeService columnTypeService) {
    this.columnTypeService = columnTypeService;
  }

  @PostMapping
  public ResponseEntity<ColumnTypeResponse> createBoard(@RequestBody ColumnTypeRequest request) {
    ColumnTypeResponse columnType = columnTypeService.save(request);
    return ResponseEntity.ok().body(columnType);
  }
}
