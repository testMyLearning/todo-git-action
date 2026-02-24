package com.todo.analytics.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "daily_task_stats", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","date"}))
@Data
public class DailyTaskStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
@Column(name="user_id",nullable=false)
    private Long userId;
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "total_tasks")
    private Integer totalTasks = 0;

    @Column(name = "completed_tasks")
    private Integer completedTasks = 0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "last_event_id")
    private UUID lastEventId;
}
