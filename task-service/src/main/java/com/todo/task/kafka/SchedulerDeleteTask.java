package com.todo.task.kafka;

import com.todo.task.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerDeleteTask {

    private final OutboxRepository repository;

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void clean(){
        Instant cleanAfterWeek = Instant.now().minus(7, ChronoUnit.DAYS);
        repository.deletePublishedBefore(cleanAfterWeek);
        log.info("задачи с датой публикации до {} удалены",cleanAfterWeek);
    }
}
