package com.group5.taskFlow.controller;

import com.group5.taskFlow.model.ActivityLogsModels;
import com.group5.taskFlow.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @Autowired
    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<ActivityLogsModels>> getActivityLogsByCardId(@PathVariable UUID cardId) {
        return ResponseEntity.ok(activityLogService.findByCardId(cardId));
    }
}
