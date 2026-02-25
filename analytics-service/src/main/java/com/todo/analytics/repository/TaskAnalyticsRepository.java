package com.todo.analytics.repository;

import com.todo.analytics.dto.DeadlineAlertDto;
import com.todo.analytics.entity.DailyTaskStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskAnalyticsRepository extends JpaRepository<DailyTaskStats, Long> {

    Optional<DailyTaskStats> findByUserIdAndDate(Long userId, LocalDate date);

    List<DailyTaskStats> findByUserIdOrderByDateDesc(Long userId);


}
