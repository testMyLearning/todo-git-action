package com.todo.task.controller;

import com.todo.common.dto.CreateTaskRequest;
import com.todo.common.dto.TaskDto;
import com.todo.common.dto.UpdateTaskRequest;
import com.todo.common.dto.ErrorResponse;
import com.todo.common.dtoAsync.PageResponse;
import com.todo.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
@Tag(name = "Task service", description = "Контроллер для управления задачами")
@RestController
@RequestMapping("/api/tasks")  // убрали /async из пути (можно оставить, но для простоты уберём)
@RequiredArgsConstructor
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;  // тип изменён

    @Operation(summary = "Получение списка всех задач пользователя",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "список всех задач пользователя",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка загрузки задач пользователя",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserTasks(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
            PageResponse<TaskDto> result = taskService.findAll(userId, page, size, sortBy, direction);
            return ResponseEntity.ok(result);
    }

    @Operation(summary = "Создание задачи пользователя",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно создана",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка создания задачи пользователя",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(
            @Valid @RequestBody CreateTaskRequest request,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId) {
            TaskDto result = taskService.create(request, userId);
            return ResponseEntity.ok(result);

    }

    @Operation(summary = "Обновление задачи пользователя",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно обновлена",
            content = @Content(schema = @Schema(implementation = TaskDto.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка обновления задачи пользователя",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping
    public ResponseEntity<?> update(
            @Valid @RequestBody UpdateTaskRequest request,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId) {
            TaskDto result = taskService.update(request, userId);
            return ResponseEntity.ok(result);

    }
}