package com.todo.task.controller;

import com.todo.common.dto.CreateTaskRequest;
import com.todo.common.dto.TaskDto;
import com.todo.common.dto.UpdateTaskRequest;
import com.todo.common.dtoAsync.PageResponse;
import com.todo.task.service.TaskService;
import com.todo.task.service.TaskServiceAsync;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;
    private final TaskServiceAsync taskServiceAsync;

    /**
     * Получить все задачи текущего пользователя
     * GET /api/tasks
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping("/async")
    public CompletableFuture<ResponseEntity<PageResponse<TaskDto>>> getUserTasks(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
            ) {
        return taskServiceAsync.findAll(userId, page, size, sortBy, direction).thenApply(ResponseEntity::ok)
                .exceptionally(throwable -> {
                    log.error("Error getting tasks for user {}", userId, throwable);
                    return ResponseEntity.status(500).build();
                });
    }


    /**
     * Получить задачу по ID
     * GET /api/tasks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(taskService.getTask(id, userId));
    }

    /**
     * Создать новую задачу
     * POST /api/tasks
     */
    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(taskService.createTask(request, userId));
    }
@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping("/async")
public CompletableFuture<ResponseEntity<TaskDto>> create(
        @Valid @RequestBody CreateTaskRequest request,
        @RequestHeader("X-User-Id") Long userId
){
        return taskServiceAsync.create(request,userId).thenApply(ResponseEntity::ok).exceptionally(
                trownable->{
                    log.error("ошибка в создании задачи", trownable);
                    return ResponseEntity.status(500).build();
                }
        );
}
    /**
     * Обновить задачу
     * PATCH /api/tasks/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(taskService.updateTask(id, request, userId));
    }

    /**
     * Удалить задачу
     * DELETE /api/tasks/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") Long userId) {
        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }
}
