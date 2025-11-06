package com.group5.taskFlow.controller;

import com.group5.taskFlow.dto.ColumnRequest;
import com.group5.taskFlow.dto.ColumnResponse;
import com.group5.taskFlow.service.ColumnService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/columns")
public class ColumnController {
    private final ColumnService columnService;

    public ColumnController(ColumnService columnService) {
        this.columnService = columnService;
    }

    @PostMapping
    public ResponseEntity<ColumnResponse> createColumn(@RequestBody ColumnRequest columnRequest) {
        ColumnResponse newColumn = columnService.save(columnRequest);
        return new ResponseEntity<>(newColumn, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ColumnResponse>> getAllColumns() {
        List<ColumnResponse> columns = columnService.findAll();
        return new ResponseEntity<>(columns, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColumnResponse> getColumnById(@PathVariable("id") UUID id) {
        ColumnResponse column = columnService.findById(id);
        return new ResponseEntity<>(column, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColumnResponse> updateColumn(@PathVariable("id") UUID id, @RequestBody ColumnRequest columnRequest) {
        ColumnResponse updatedColumn = columnService.update(id, columnRequest);
        return new ResponseEntity<>(updatedColumn, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColumn(@PathVariable("id") UUID id) {
        columnService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
