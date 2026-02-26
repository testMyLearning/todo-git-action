package com.todo.user.controller;

import com.todo.common.dto.AuthRequest;
import com.todo.common.dto.AuthResponse;
import com.todo.common.dto.UserDto;
import com.todo.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

/**
 * REST контроллер для пользователей
 *
 * Зачем:
 * 1. Обрабатывает HTTP запросы
 * 2. Использует DTO для ввода/вывода
 * 3. Валидация входных данных
 */
@Tag(name="UserController", description="Работа с пользователем: регистрации, авторизации... ")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Регистрация нового пользователя
     * POST /api/users/register
     */
    @Operation(summary = "Регистрация пользователя")
    @ApiResponse(
                    responseCode = "200",
                    description = "пользователь зарегистрирован")

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = userService.register(request.name(),
                request.email(),
                request.password()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Вход пользователя
     * POST /api/users/login
     */

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = userService.login(
                request.email(),
                request.password()
        );
        return ResponseEntity.ok(response);
    }

    //TODO нет методов апдейт и делет





    /**
     * Получение пользователя по ID (для внутренних вызовов)
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Получение пользователя по email (для внутренних вызовов)
     * GET /api/users/by-email?email=...
     */
    @GetMapping("/by-email")
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }
}