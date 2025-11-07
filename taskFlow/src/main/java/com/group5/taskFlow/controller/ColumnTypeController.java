package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.ColumnTypeRequest;
import com.group5.taskFlow.dto.ColumnTypeResponse;
import com.group5.taskFlow.service.ColumnTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

  @GetMapping
  public ResponseEntity<List<ColumnTypeResponse>> getAllColumnTypes() {
    List<ColumnTypeResponse> columnTypes = columnTypeService.findAll();
    return new ResponseEntity<>(columnTypes, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ColumnTypeResponse> getColumnTypeById(@PathVariable("id") UUID id) {
    ColumnTypeResponse columnType = columnTypeService.findById(id);
    return new ResponseEntity<>(columnType, HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ColumnTypeResponse> updateColumnType(@PathVariable("id") UUID id, @RequestBody ColumnTypeRequest columnTypeRequest) {
    ColumnTypeResponse updatedColumnType = columnTypeService.update(id, columnTypeRequest);
    return new ResponseEntity<>(updatedColumnType, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteColumnType(@PathVariable("id") UUID id) {
    columnTypeService.deleteById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
