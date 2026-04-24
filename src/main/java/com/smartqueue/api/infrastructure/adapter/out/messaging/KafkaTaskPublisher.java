package com.smartqueue.api.infrastructure.adapter.out.messaging;

import com.smartqueue.api.application.port.out.TaskPublisherPort;
import com.smartqueue.api.domain.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTaskPublisher implements TaskPublisherPort {

    private final KafkaTemplate<String, Task> kafkaTemplate;

    @Value("${app.queue.main-topic}")
    private String mainTopic;

    @Value("${app.queue.retry-topic}")
    private String retryTopic;

    @Value("${app.queue.dlq-topic}")
    private String dlqTopic;

    @Override
    public void publish(Task task) {
        log.debug("Publishing task {} to main topic {}", task.getId(), mainTopic);
        kafkaTemplate.send(mainTopic, task.getId().toString(), task);
    }

    @Override
    public void publishToRetry(Task task) {
        log.debug("Publishing task {} to retry topic {}", task.getId(), retryTopic);
        kafkaTemplate.send(retryTopic, task.getId().toString(), task);
    }

    @Override
    public void publishToDlq(Task task) {
        log.debug("Publishing task {} to DLQ topic {}", task.getId(), dlqTopic);
        kafkaTemplate.send(dlqTopic, task.getId().toString(), task);
    }
}
