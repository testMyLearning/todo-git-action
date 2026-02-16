package com.todo.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateTaskRequest(
        @NotBlank(message = "Название задачи обязательно")
        String name,
        @NotBlank(message = "Описание обязательно")
        String description,

        @NotNull(message = "Дедлайн обязателен")
        LocalDate deadline
) {}
