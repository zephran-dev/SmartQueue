package com.smartqueue.api.application.usecase;

import com.smartqueue.api.application.port.in.ProcessTaskUseCase;
import com.smartqueue.api.application.port.out.TaskPublisherPort;
import com.smartqueue.api.application.port.out.TaskRepositoryPort;
import com.smartqueue.api.domain.exception.TaskException;
import com.smartqueue.api.domain.model.Task;
import com.smartqueue.api.domain.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService implements ProcessTaskUseCase {

    private final TaskPublisherPort publisherPort;
    private final TaskRepositoryPort repositoryPort;

    @Value("${app.queue.max-retries:3}")
    private int maxRetries;

    @Override
    public Task submitTask(String payload) {
        log.info("Submitting new task with payload: {}", payload);
        Task task = Task.builder()
                .id(UUID.randomUUID())
                .payload(payload)
                .status(TaskStatus.PENDING)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        task = repositoryPort.save(task);
        publisherPort.publish(task);
        log.info("Task {} submitted successfully", task.getId());
        return task;
    }

    @Override
    public void processTask(Task task) {
        log.info("Processing task {}", task.getId());
        try {
            task.setStatus(TaskStatus.PROCESSING);
            task.setUpdatedAt(LocalDateTime.now());
            repositoryPort.save(task);

            // Simulate processing logic
            if (task.getPayload().contains("error")) {
                throw new RuntimeException("Simulated processing error for payload");
            }

            task.setStatus(TaskStatus.COMPLETED);
            task.setUpdatedAt(LocalDateTime.now());
            repositoryPort.save(task);
            log.info("Task {} processed successfully", task.getId());
        } catch (Exception e) {
            handleTaskFailure(task, e);
        }
    }

    @Override
    public void handleTaskFailure(Task task, Exception e) {
        log.error("Task {} failed processing: {}", task.getId(), e.getMessage());
        task.setErrorMessage(e.getMessage());
        task.setUpdatedAt(LocalDateTime.now());

        if (task.getRetryCount() < maxRetries) {
            task.setRetryCount(task.getRetryCount() + 1);
            task.setStatus(TaskStatus.PENDING);
            repositoryPort.save(task);
            log.info("Publishing task {} to retry topic (Attempt {}/{})", task.getId(), task.getRetryCount(), maxRetries);
            publisherPort.publishToRetry(task);
        } else {
            task.setStatus(TaskStatus.DLQ);
            repositoryPort.save(task);
            log.error("Max retries reached for task {}. Moving to DLQ.", task.getId());
            publisherPort.publishToDlq(task);
        }
    }

    @Override
    public Task getTaskStatus(UUID taskId) {
        return repositoryPort.findById(taskId)
                .orElseThrow(() -> new TaskException("Task not found with ID: " + taskId));
    }
}
