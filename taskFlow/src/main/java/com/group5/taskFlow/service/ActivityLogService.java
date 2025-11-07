package com.group5.taskFlow.service;

import com.group5.taskFlow.model.ActivityLogsModels;
import com.group5.taskFlow.repository.ActivityLogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityLogService {

    private final ActivityLogsRepository activityLogsRepository;

    @Autowired
    public ActivityLogService(ActivityLogsRepository activityLogsRepository) {
        this.activityLogsRepository = activityLogsRepository;
    }

    public List<ActivityLogsModels> findByCardId(UUID cardId) {
        return activityLogsRepository.findByCardId(cardId);
    }
}
