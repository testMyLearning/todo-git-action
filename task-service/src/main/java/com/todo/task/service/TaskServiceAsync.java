package com.todo.task.service;

import com.todo.common.dto.CreateTaskRequest;
import com.todo.common.dto.TaskDto;
import com.todo.common.dtoAsync.PageResponse;
import com.todo.task.entity.Task;
import com.todo.task.mapper.TaskMapper;
import com.todo.task.repository.TaskRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceAsync {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
@Async
    public CompletableFuture<PageResponse<TaskDto>> findAll(Long userId,
                                                            int page,
                                                            int size,
                                                            String sortBy,
                                                            String sortDirection){
    log.info("Пришел запрос в асинхронный findall от {} в потоке {}",userId,Thread.currentThread().getName());
    Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);
    CompletableFuture<Page<Task>> taskPage = taskRepository.findByUserIdAsync(userId,pageable);
        return mapToPageResponse(taskPage,taskMapper::toDto);
        }

@Async
    public CompletableFuture<TaskDto> create(@Valid CreateTaskRequest request, Long userId) {
        Task task = taskMapper.toEntity(request);
        task.setUserId(userId);
        return taskRepository.saveAsync(task).thenApply(taskMapper::toDto);
        }
    }


