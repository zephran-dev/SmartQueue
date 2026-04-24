package com.smartqueue.api.infrastructure.adapter.in.messaging;

import com.smartqueue.api.application.port.in.ProcessTaskUseCase;
import com.smartqueue.api.domain.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTaskConsumer {

    private final ProcessTaskUseCase processTaskUseCase;

    @KafkaListener(topics = "${app.queue.main-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMain(Task task, Acknowledgment acknowledgment) {
        log.info("Received task {} from main topic", task.getId());
        processTask(task, acknowledgment);
    }

    @KafkaListener(topics = "${app.queue.retry-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRetry(Task task, Acknowledgment acknowledgment) {
        log.info("Received task {} from retry topic", task.getId());
        processTask(task, acknowledgment);
    }

    private void processTask(Task task, Acknowledgment acknowledgment) {
        try {
            processTaskUseCase.processTask(task);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing task {}", task.getId(), e);
            // In manual ack mode, if we don't acknowledge, it could be re-delivered depending on config, 
            // but we are using custom retry to our retry topic, so we acknowledge and let our service handle DLQ/retry.
            acknowledgment.acknowledge();
        }
    }
}
