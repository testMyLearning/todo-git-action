package com.todo.task.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.common.dto.CreateTaskRequest;
import com.todo.common.dto.TaskDto;
import com.todo.common.dto.TaskFilterDto;
import com.todo.common.dto.UpdateTaskRequest;
import com.todo.common.dtoAsync.PageResponse;
import com.todo.common.enums.StatusTask;
import com.todo.common.event.TaskEvent;
import com.todo.task.entity.OutboxEvent;
import com.todo.task.entity.Task;
import com.todo.task.mapper.TaskMapper;
import com.todo.task.repository.OutboxRepository;
import com.todo.task.repository.TaskRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceAsync {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<PageResponse<TaskDto>> findAll(Long userId,
                                                            int page,
                                                            int size,
                                                            String sortBy,
                                                            String sortDirection) {
        log.info("Пришел запрос в асинхронный findall от {} в потоке {}", userId, Thread.currentThread().getName());
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return taskRepository.findByUserIdAsync(userId, pageable).thenApply(taskPage -> {
            PageResponse<TaskDto> response = new PageResponse<>();
            response.setContent(taskMapper.toDtoList(taskPage.getContent()));
            response.setPage(taskPage.getNumber());
            response.setSize(taskPage.getSize());
            response.setTotalElements(taskPage.getTotalElements());
            response.setTotalPages(taskPage.getTotalPages());
            response.setFirstPage(taskPage.isFirst());
            response.setLastPage(taskPage.isLast());
            return response;
        });
    }

    @Async
    @Transactional
    public CompletableFuture<TaskDto> create(@Valid CreateTaskRequest request, Long userId) {
        Task task = taskMapper.toEntity(request);
        task.setUserId(userId);
        return taskRepository.saveAsync(task).thenApply(savedTask -> {
            // Создаем событие
            TaskEvent event = new TaskEvent(
                    savedTask.getId(),
                    userId,
                    savedTask.getName(),
                    savedTask.getStatus().name(),
                    savedTask.getDeadline(),
                    "TASK_CREATED",
                    "task-service"
            );

            try {
                // Сохраняем в outbox
                OutboxEvent outboxEvent = OutboxEvent.builder()
                        .aggregateId(savedTask.getId().toString())
                        .eventType(event.getEventType())
                        .service(event.getService())
                        .payload(objectMapper.writeValueAsString(event))
                        .createdAt(LocalDateTime.now())
                        .build();

                outboxRepository.save(outboxEvent);
                log.info("Saved event to outbox for task: {}", savedTask.getId());

            } catch (JsonProcessingException e) {
                log.error("Failed to serialize event for task: {}", savedTask.getId(), e);
                throw new RuntimeException("Error saving event to outbox");
            }

            log.info("Task created with id: {}", savedTask.getId());
            return taskMapper.toDto(savedTask);
        });
    }
    @Async
    @Transactional
    public CompletableFuture<TaskDto> update(@Valid UpdateTaskRequest request, Long userId) {
        return taskRepository.findByIdAsync(request.id()).thenApply(task -> {
            if (task == null || !task.getUserId().equals(userId)) {
                throw new RuntimeException("Task not found or access denied");
            }

            // Обновляем поля
            if (request.name() != null && !request.name().isBlank()) {
                task.setName(request.name());
            }
            if (request.description() != null) {
                task.setDescription(request.description());
            }
            if (request.deadline() != null) {
                task.setDeadline(request.deadline());
            }
            if (request.status() != null) {
                task.setStatus(StatusTask.valueOf(request.status()));
            }

            return taskRepository.saveAsync(task).join();
        }).thenApply(updatedTask -> {
            // Отправляем событие об обновлении
            TaskEvent event = new TaskEvent(
                    updatedTask.getId(),
                    userId,
                    updatedTask.getName(),
                    updatedTask.getStatus().name(),
                    updatedTask.getDeadline(),
                    "TASK_UPDATED",
                    "task-service"
            );

            try {
                OutboxEvent outboxEvent = OutboxEvent.builder()
                        .aggregateId(updatedTask.getId().toString())
                        .eventType(event.getEventType())
                        .service(event.getService())
                        .payload(objectMapper.writeValueAsString(event))
                        .createdAt(LocalDateTime.now())
                        .build();

                outboxRepository.save(outboxEvent);
                log.info("Saved update event to outbox for task: {}", updatedTask.getId());

            } catch (JsonProcessingException e) {
                log.error("Failed to serialize update event for task: {}", updatedTask.getId(), e);
            }

            return taskMapper.toDto(updatedTask);
        });
    }

    public boolean taskHasUser(Task task, Long userId){
        return Objects.equals(task.getUserId(), userId);
    }
}


