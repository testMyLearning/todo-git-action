package com.todo.common.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskDto(
        UUID id,
        String name,
        String description,
        String status,
        LocalDate deadline,
        LocalDateTime dateTimeOfCompletion,
        Long userId  // только ID пользователя, не весь объект!
) {}
