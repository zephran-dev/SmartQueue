package com.smartqueue.api.infrastructure.adapter.out.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartqueue.api.application.port.out.TaskRepositoryPort;
import com.smartqueue.api.domain.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisTaskRepository implements TaskRepositoryPort {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String KEY_PREFIX = "task:";
    private static final long TTL_HOURS = 24;

    @Override
    public Task save(Task task) {
        redisTemplate.opsForValue().set(
                KEY_PREFIX + task.getId().toString(),
                task,
                TTL_HOURS,
                TimeUnit.HOURS
        );
        return task;
    }

    @Override
    public Optional<Task> findById(UUID id) {
        Object value = redisTemplate.opsForValue().get(KEY_PREFIX + id.toString());
        if (value != null) {
            return Optional.of(objectMapper.convertValue(value, Task.class));
        }
        return Optional.empty();
    }

    @Override
    public void delete(UUID id) {
        redisTemplate.delete(KEY_PREFIX + id.toString());
    }
}
