package com.todo.task.service;

import com.todo.common.dto.CreateTaskRequest;
import com.todo.common.dto.TaskDto;
import com.todo.common.dto.UpdateTaskRequest;
import com.todo.common.dto.UserDto;
import com.todo.common.enums.StatusTask;
import com.todo.task.entity.Task;
import com.todo.task.mapper.TaskMapper;
import com.todo.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы с задачами
 * Теперь с использованием маппера!
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;  // ← инжектим маппер

    /**
     * Получить все задачи пользователя
     */
    public List<TaskDto> getUserTasks(Long userId) {
        log.info("Getting tasks for user: {}", userId);


        List<Task> tasks = taskRepository.findByUserId(userId);
        return taskMapper.toDtoList(tasks);  // ← используем маппер для списка
    }

    /**
     * Получить задачу по ID
     */
    public TaskDto getTask(UUID taskId, Long userId) {
        Task task = findTaskForUser(taskId, userId);
        return taskMapper.toDto(task);  // ← используем маппер
    }

    /**
     * Создать новую задачу
     */
    @Transactional
    public TaskDto createTask(CreateTaskRequest request, Long userId) {
        log.info("Creating task for user: {}", userId);

        // Используем маппер для создания Entity
        Task task = taskMapper.toEntity(request);
        task.setUserId(userId);
        task = taskRepository.save(task);

        log.info("Task created with id: {}", task.getId());
        return taskMapper.toDto(task);
    }

    /**
     * Обновить задачу
     */
    @Transactional
    public TaskDto updateTask(UUID taskId, UpdateTaskRequest request, Long userId) {
        log.info("Updating task: {} for user: {}", taskId, userId);

        // 1. Проверяем права
        Task task = findTaskForUser(taskId, userId);

        // 2. Обновляем простые поля
        if (request.name() != null && !request.name().isBlank()) {
            task.setName(request.name());
        }
        if (request.description() != null && !request.description().isBlank()) {
            task.setDescription(request.description());
        }
        if (request.deadline() != null) {
            // Можно добавить валидацию дедлайна
            task.setDeadline(request.deadline());
        }

        // 3. Обновляем статус с бизнес-логикой
        if (request.status() != null) {
            updateTaskStatus(task, request.status());
        }

        task = taskRepository.save(task);
        log.info("Task updated successfully");

        return taskMapper.toDto(task);
    }

    private void updateTaskStatus(Task task, String newStatusStr) {
        StatusTask newStatus = StatusTask.valueOf(newStatusStr);

        // Если статус не меняется - ничего не делаем
        if (task.getStatus() == newStatus) return;

        // Логика переключения статуса
        if (newStatus == StatusTask.COMPLETED) {
            // Активируем дату завершения
            task.setDateTimeOfCompletion(LocalDateTime.now());
        } else {
            // Убираем дату завершения
            task.setDateTimeOfCompletion(null);
        }

        task.setStatus(newStatus);
    }

    /**
     * Удалить задачу
     */
    @Transactional
    public void deleteTask(UUID taskId, Long userId) {
        Task task = findTaskForUser(taskId, userId);
        taskRepository.delete(task);
        log.info("Task deleted: {}", taskId);
    }

    /**
     * Найти задачу и проверить принадлежность пользователю
     */
    private Task findTaskForUser(UUID taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Task does not belong to user: " + userId);
        }

        return task;
    }
}