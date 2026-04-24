package com.smartqueue.api.infrastructure.adapter.in.web;

import com.smartqueue.api.domain.model.Task;
import com.smartqueue.api.domain.model.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TaskResponse {
    private UUID id;
    private TaskStatus status;
    private int retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskResponse from(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .status(task.getStatus())
                .retryCount(task.getRetryCount())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
