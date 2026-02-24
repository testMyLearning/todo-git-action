package com.todo.common.event;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class TaskCreatedEvent extends BaseEvent {
    private UUID taskId;
    private Long userId;
    private String taskName;
    private String status;
    private LocalDate deadline;

    public TaskCreatedEvent(UUID taskId, Long userId, String taskName, String status, LocalDate deadline) {
        super(taskId.toString(),"TASK_CREATED", Instant.now(),"task-service");
        this.taskId = taskId;
        this.userId = userId;
        this.taskName = taskName;
        this.status = status;
        this.deadline = deadline;
    }
}
