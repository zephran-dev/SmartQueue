package com.smartqueue.api.application.port.out;

import com.smartqueue.api.domain.model.Task;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepositoryPort {
    Task save(Task task);
    Optional<Task> findById(UUID id);
    void delete(UUID id);
}
