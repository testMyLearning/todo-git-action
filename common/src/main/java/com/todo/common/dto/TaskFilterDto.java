package com.todo.common.dto;

import jakarta.annotation.Nullable;

import java.time.LocalDate;

public record TaskFilterDto(
        @Nullable
        String status,
        @Nullable
        Long userId,
        @Nullable
        LocalDate deadline
) {}
