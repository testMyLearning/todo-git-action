package com.todo.common.event;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class TaskEvent extends BaseEvent {
    private UUID taskId;
    private Long userId;
    private String taskName;
    private String status;
    private LocalDate deadline;

    public TaskEvent(UUID taskId, Long userId, String taskName, String status, LocalDate deadline, String eventType, String service) {
        super(taskId.toString(),eventType, Instant.now(),service);
        this.taskId = taskId;
        this.userId = userId;
        this.taskName = taskName;
        this.status = status;
        this.deadline = deadline;
    }
}
