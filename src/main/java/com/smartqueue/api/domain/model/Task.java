package com.smartqueue.api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private UUID id;
    private String payload;
    private TaskStatus status;
    private int retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String errorMessage;
}
