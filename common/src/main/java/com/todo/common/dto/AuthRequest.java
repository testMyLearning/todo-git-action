package com.todo.common.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record AuthRequest(
        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный email")
        String email,

        @NotBlank(message = "Пароль обязателен")
        String password
) {}
