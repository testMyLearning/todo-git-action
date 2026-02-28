package com.todo.task.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.task.entity.OutboxEvent;
import com.todo.task.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 15000)
    @Transactional
    public void publishEvents(){
        List<OutboxEvent> events = outboxRepository.findUnpublishedEvents(100);
         if(events.isEmpty()){
             return;
         }
        log.info("📤 Publishing {} events to Kafka", events.size());

        for (OutboxEvent event : events) {
            try {
                // Отправляем в Kafka
                CompletableFuture<SendResult<String, String>> future =
                        kafkaTemplate.send(event.getEventType(), event.getPayload());

                // Асинхронно обрабатываем результат
                future.whenComplete((result, ex) -> {
                    if (ex == null) {
                        // Успешно отправили
                        markAsPublished(event);
                        log.debug("✅ Event {} published to Kafka", event.getId());
                    } else {
                        // Ошибка отправки
                        log.error("❌ Failed to publish event {}", event.getId(), ex);
                        incrementRetry(event);
                    }
                });

            } catch (Exception e) {
                log.error("❌ Exception publishing event {}", event.getId(), e);
                incrementRetry(event);
            }
        }
    }

    @Transactional
    public void markAsPublished(OutboxEvent event) {
        event.setPublishedAt(Instant.now());
        outboxRepository.save(event);
    }

    @Transactional
    public void incrementRetry(OutboxEvent event) {
        event.setRetryCount(event.getRetryCount() + 1);
        // Если превысили лимит, помечаем как опубликованное (но записываем ошибку)
        if (event.getRetryCount() > 10) {
            log.error("🔥 Event {} failed after 10 retries. Giving up.", event.getId());
            event.setPublishedAt(Instant.now());
        }
        outboxRepository.save(event);
    }
    }



