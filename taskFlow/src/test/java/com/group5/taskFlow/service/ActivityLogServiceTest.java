package com.group5.taskFlow.service;

import com.group5.taskFlow.model.ActivityLogsModels;
import com.group5.taskFlow.model.BoardModels;
import com.group5.taskFlow.model.UserModels;
import com.group5.taskFlow.model.enums.EventType;
import com.group5.taskFlow.repository.ActivityLogRepository;
import com.group5.taskFlow.service.ActivityLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ActivityLogServiceTest {

    @Mock
    private ActivityLogRepository activityLogRepository;

    @InjectMocks
    private ActivityLogService activityLogService;

    @Test
    void logActivity() {
        UserModels user = new UserModels();
        user.setId(UUID.randomUUID());

        BoardModels board = new BoardModels();
        board.setId(UUID.randomUUID());

        activityLogService.logActivity(EventType.BOARD_CREATED, "Board created", user, board);

        verify(activityLogRepository).save(any(ActivityLogsModels.class));
    }
}
