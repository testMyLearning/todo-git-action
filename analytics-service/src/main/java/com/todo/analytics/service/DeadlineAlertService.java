package com.todo.analytics.service;

import com.todo.analytics.dto.DeadlineAlertDto;
import com.todo.analytics.entity.DeadlineAlert;
import com.todo.analytics.repository.DeadlineAlertRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Data
@Slf4j
public class DeadlineAlertService {

    private final DeadlineAlertRepository repository;

    @Scheduled(cron = "0 0 * * * *") // каждый час
    @Transactional
    public void processUpcomingDeadlines() {
        log.info("⏰ Checking deadlines for next 24 hours...");

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        // Берем только те, по которым еще не отправляли уведомления
        List<DeadlineAlert> alerts = repository
                .findTasksWithDeadlineBetween(today, tomorrow);

        if (alerts.isEmpty()) {
            log.info("No new deadlines to notify");
            return;
        }

        log.info("Found {} deadlines to notify", alerts.size());

        alerts.stream().forEach(alert -> {
            alert.setAlertSent(true);
            alert.setUpdatedAt(LocalDateTime.now());
            repository.save(alert);
            log.info("имитация отправки сообщения пользователю каждый час");
        });
    }

    public List<DeadlineAlertDto> getTasksDeadlineInNext24Hours(){
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate today = LocalDate.now();
        List<DeadlineAlert> alerts = repository
                .findTasksWithDeadlineBetween(today, tomorrow);

        return alerts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    private DeadlineAlertDto toDto(DeadlineAlert alert) {
        return new DeadlineAlertDto(
                alert.getUserId(),
                alert.getTaskId(),
                alert.getTaskName(),
                alert.getDeadline()
        );
    }
    }


