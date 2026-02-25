package com.todo.task.controller;

import com.todo.common.dto.CreateTaskRequest;
import com.todo.common.dto.TaskDto;
import com.todo.common.dto.UpdateTaskRequest;
import com.todo.common.dtoAsync.PageResponse;
import com.todo.task.service.TaskServiceAsync;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/tasks/async")
@RequiredArgsConstructor
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final TaskServiceAsync taskServiceAsync;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)

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


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<TaskDto>> create(
            @Valid @RequestBody CreateTaskRequest request,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return taskServiceAsync.create(request, userId).thenApply(ResponseEntity::ok).exceptionally(
                trownable -> {
                    log.error("ошибка в создании задачи", trownable);
                    return ResponseEntity.status(500).build();
                }
        );
    }

    public CompletableFuture<ResponseEntity<Void>> update(@Valid @RequestBody UpdateTaskRequest requset,
                       @RequestHeader("X-user-id") Long userId) {
return taskServiceAsync.update(requset,userId).thenApply(ResponseEntity::ok).exceptionally(exception->{
    log.error("ошибка в контроллере обновления задач",exception);
    return ResponseEntity.status(500).build();
});



    }
}