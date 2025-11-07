package com.group5.taskFlow.service;

import com.group5.taskFlow.model.ActivityLogsModels;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.EventType;
import com.group5.taskFlow.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Autowired
    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional
    public void logActivity(EventType eventType, String details, UserModels user, BoardModels board) {
        ActivityLogsModels log = new ActivityLogsModels();
        log.setEventType(eventType);
        log.setDetails(details);
        log.setUser(user);
        log.setBoard(board);
        activityLogRepository.save(log);
    }

    public List<ActivityLogsModels> findByBoardId(UUID boardId) {
        return activityLogRepository.findByBoardId(boardId);
    }
}