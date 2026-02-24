package com.todo.analytics.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic taskCreatedTopic() {
        return TopicBuilder.name("TASK_CREATED")
                .partitions(6)  // 6 партиций для параллелизма
                .replicas(3)     // 3 реплики для отказоустойчивости
                .build();
    }

    @Bean
    public NewTopic taskCompletedTopic() {
        return TopicBuilder.name("TASK_COMPLETED")
                .partitions(6)
                .replicas(3)
                .build();
    }
}
