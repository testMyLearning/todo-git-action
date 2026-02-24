package com.todo.analytics.controller;

import com.todo.analytics.dto.DeadlineAlertDto;
import com.todo.analytics.entity.DailyTaskStats;
import com.todo.analytics.repository.TaskAnalyticsRepository;
import com.todo.analytics.service.DeadlineAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final TaskAnalyticsRepository analyticsRepository;
    private final DeadlineAlertService deadlineAlertService;

    @GetMapping("/users/{userId}/daily")
    public DailyTaskStats getUserDailyStats(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return analyticsRepository.findByUserIdAndDate(userId, date)
                .orElse(new DailyTaskStats());
    }

    @GetMapping("/users/{userId}/history")
    public List<DailyTaskStats> getUserStatsHistory(@PathVariable Long userId) {
        return analyticsRepository.findByUserIdOrderByDateDesc(userId);
    }

    @GetMapping("/deadlines/next-24h")
    public List<DeadlineAlertDto> getDeadlinesNext24Hours() {
        return deadlineAlertService.getTasksDeadlineInNext24Hours();
    }
}
