package com.todo.analytics.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name="deadline_alerts", uniqueConstraints = @UniqueConstraint(columnNames = {"task_id"}))
@Builder
@NoArgsConstructor  // Для JPA
@AllArgsConstructor
public class DeadlineAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private UUID taskId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "status")
    private String status;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "alert_sent")
    private boolean alertSent = false;
}
