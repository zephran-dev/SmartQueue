package com.smartqueue.api.application.port.out;

import com.smartqueue.api.domain.model.Task;

public interface TaskPublisherPort {
    void publish(Task task);
    void publishToRetry(Task task);
    void publishToDlq(Task task);
}
