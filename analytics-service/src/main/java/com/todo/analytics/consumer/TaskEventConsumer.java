package com.todo.analytics.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.analytics.entity.DailyTaskStats;
import com.todo.analytics.entity.DeadlineAlert;
import com.todo.analytics.repository.DeadlineAlertRepository;
import com.todo.analytics.repository.TaskAnalyticsRepository;
import com.todo.common.enums.StatusTask;
import com.todo.common.event.TaskEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TaskEventConsumer {

    private final TaskAnalyticsRepository taskAnalyticsRepository;
    private final DeadlineAlertRepository alertRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "TASK_CREATED", groupId = "analytics-service")
    @Transactional
    public void handleTaskCreated(List<String> batch, Acknowledgment ack) {

        try {
            for (String message : batch) {
                TaskEvent event = objectMapper.readValue(message, TaskEvent.class);

                // Получаем статистику для этого пользователя на сегодня
                LocalDate today = LocalDate.now();
                DailyTaskStats stats = getOrCreateStats(event.getUserId(), today);

                // Проверяем, не обрабатывали ли уже ЭТО КОНКРЕТНОЕ событие
                if (event.getEventId().equals(stats.getLastEventId())) {
                    log.info("Событие {} уже обработано для пользователя {}, пропускаем",
                            event.getEventId(), event.getUserId());
                    return;
                }

                // Если не обрабатывали - обрабатываем
                taskCreatedForStats(event, stats);  // Передаем уже полученную stats
                taskCreatedForAlert(event);

            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Ошибка при обработке события TASK_CREATED", e);
        }
    }

    @KafkaListener(topics = "TASK_UPDATED", groupId = "analytics-service")
    @Transactional
    public void handleTaskUpdated(List<String> batch, Acknowledgment ack) {

        try {
            for (String message : batch) {
                TaskEvent event = objectMapper.readValue(message, TaskEvent.class);

                // Получаем статистику для этого пользователя на сегодня
                LocalDate today = LocalDate.now();
                DailyTaskStats stats = getOrCreateStats(event.getUserId(), today);

                // Проверяем, не обрабатывали ли уже ЭТО КОНКРЕТНОЕ событие
                if (event.getEventId().equals(stats.getLastEventId())) {
                    log.info("Событие update {} уже обработано для пользователя {}, пропускаем",
                            event.getEventId(), event.getUserId());
                    return;
                }

                // Если не обрабатывали - обрабатываем
                taskUpdatedForStats(event, stats);  // Передаем уже полученную stats
                taskUpdatedForAlert(event);

            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Ошибка при обработке события TASK_UPDATED", e);
        }
    }


    //    @KafkaListener(topics = "TASK_COMPLETED", groupId = "analytics-service")
//    @Transactional
//    public void handleTaskCompleted(String message) {
//        try {
//            TaskCompletedEvent event = objectMapper.readValue(message, TaskCompletedEvent.class);
//            log.info("📊 Получено событие TASK_COMPLETED: пользователь {}, задача {}",
//                    event.getUserId(), event.getTaskId());
//
//            LocalDate today = LocalDate.now();
//
//            DailyTaskStats stats = getOrCreateStats(event.getUserId(), today);
//            stats.setCompletedTasks(stats.getCompletedTasks() + 1);
//            stats.setLastUpdated(LocalDateTime.now());
//
//            taskAnalyticsRepository.save(stats);
//
//        } catch (Exception e) {
//            log.error("❌ Ошибка в обработке события TASK_COMPLETED", e);
//        }
//    }
    private DailyTaskStats getOrCreateStats(Long userId, LocalDate date) {
        return taskAnalyticsRepository.findByUserIdAndDate(userId, date)
                .orElseGet(() -> {
                    DailyTaskStats stats = new DailyTaskStats();
                    stats.setUserId(userId);
                    stats.setDate(date);
                    stats.setTotalTasks(0);
                    stats.setCompletedTasks(0);
                    return stats;
                });
    }

    private void taskCreatedForStats(TaskEvent event, DailyTaskStats stats) {
        log.info("Получено событие от пользователя {} с задачей {} для статистики.",
                event.getTaskId(), event.getTaskName());
        stats.setLastUpdated(LocalDateTime.now());
        stats.setTotalTasks(stats.getTotalTasks() + 1);
        stats.setLastEventId(event.getEventId());
        taskAnalyticsRepository.save(stats);
        log.info("аналитика сохранена из TASK_CREATED");

    }

    private void taskCreatedForAlert(TaskEvent event) {
        log.info("Получено событие от пользователя {} с задачей {} для алерта",
                event.getTaskId(), event.getTaskName());
        LocalDate today = LocalDate.now();
        DeadlineAlert alert = DeadlineAlert.builder()
                .taskId(event.getTaskId())
                .userId(event.getUserId())
                .taskName(event.getTaskName())
                .deadline(event.getDeadline())
                .status(event.getStatus())
                .alertSent(false)
                .updatedAt(LocalDateTime.now())
                .build();
        alertRepository.save(alert);
        log.info("deadline сохранен из TASK_CREATED");
    }

    private void taskUpdatedForStats(TaskEvent event, DailyTaskStats stats) {
        log.info("Получено событие от пользователя {} с задачей {} для статистики.",
                event.getTaskId(), event.getTaskName());
        if(event.getStatus().equals("COMPLETED")){
            stats.setCompletedTasks(stats.getCompletedTasks()+1);
        }
        stats.setLastUpdated(LocalDateTime.now());
        stats.setLastEventId(event.getEventId());
        taskAnalyticsRepository.save(stats);
        log.info("аналитика сохранена из TASK_UPDATE");

    }

    private void taskUpdatedForAlert(TaskEvent event) {
        log.info("Получено событие от пользователя {} с задачей {} для апдейта алерта",
                event.getTaskId(), event.getTaskName());
        LocalDate today = LocalDate.now();
        DeadlineAlert alert = alertRepository.findByTaskId(event.getTaskId()).orElseThrow(() -> new RuntimeException("ошибка в поиске алерта в методе апдейт"));
        if (event.getStatus().equals("COMPLETED")) {
            log.info("Задача уже завершена");
            return;
        }
        if (!event.getDeadline().equals(alert.getDeadline())) {
            alert.setDeadline(event.getDeadline());
            alert.setAlertSent(false);
            alert.setUpdatedAt(LocalDateTime.now());
            alertRepository.save(alert);
            log.info("Алерт обновлен, флаг отправки сброшен");
        } else {
            log.info(("Дедлайн задачи совпадает со старым дэйлайном."));


        }
    }
}







