package com.smartqueue.api.application.port.in;

import com.smartqueue.api.domain.model.Task;
import java.util.UUID;

public interface ProcessTaskUseCase {
    Task submitTask(String payload);
    void processTask(Task task);
    void handleTaskFailure(Task task, Exception e);
    Task getTaskStatus(UUID taskId);
}
