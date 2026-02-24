package com.todo.analytics.dto;

import java.time.LocalDate;
import java.util.UUID;

public record DeadlineAlertDto(Long userId,UUID taskId, String taskName, LocalDate deadline) {
}
