package com.todo.common.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateTaskRequest(
        @NotNull
        UUID id,
        @Nullable
        @Size(min = 1,max = 100, message = "Длина заголовка от 1 до 100 символов")
        String name,
        @Nullable
        @Size(min = 1,max = 100, message = "Длина заголовка от 1 до 100 символов")
        String description,
        @Nullable
        String status,
        @Nullable
        LocalDate deadline


) {
}