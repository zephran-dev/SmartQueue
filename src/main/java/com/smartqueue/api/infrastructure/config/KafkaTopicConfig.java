package com.smartqueue.api.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.queue.main-topic}")
    private String mainTopic;

    @Value("${app.queue.retry-topic}")
    private String retryTopic;

    @Value("${app.queue.dlq-topic}")
    private String dlqTopic;

    @Bean
    public NewTopic mainTopic() {
        return TopicBuilder.name(mainTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic retryTopic() {
        return TopicBuilder.name(retryTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic dlqTopic() {
        return TopicBuilder.name(dlqTopic).partitions(1).replicas(1).build();
    }
}
