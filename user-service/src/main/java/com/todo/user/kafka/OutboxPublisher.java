package com.todo.user.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.user.entity.OutboxEvent;
import com.todo.user.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 1000)  // Каждую секунду
    @Transactional
    public void publishEvents() {
        // 1. Берем неотправленные события
        List<OutboxEvent> events = outboxRepository.findUnpublishedEvents();

        if (events.isEmpty()) {
            return;
        }

        log.info("Found {} unpublished events", events.size());

        // 2. Отправляем каждое
        for (OutboxEvent event : events) {
            try {
                // Отправляем в Kafka (топик = eventType)
                kafkaTemplate.send(event.getEventType(), event.getPayload());

                // Помечаем как отправленное
                event.setPublishedAt(Instant.now());
                outboxRepository.save(event);

                log.debug("Published event {} for aggregate {}",
                        event.getEventType(), event.getAggregateId());

            } catch (Exception e) {
                log.error("Failed to publish event {}, will retry", event.getId(), e);
                // Не помечаем как отправленное — попробуем в следующий раз
            }
        }
    }
}
