package com.todo.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(
        String token,
        String type,
        String email,
        String name,
        String role
) {
    // Удобный конструктор без type (по умолчанию Bearer)
    public AuthResponse(String token, String email, String name, String role) {
        this(token, "Bearer", email, name, role);
    }
}