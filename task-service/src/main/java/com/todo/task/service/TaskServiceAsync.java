package com.todo.task.service;

import com.todo.common.dto.CreateTaskRequest;
import com.todo.common.dto.TaskDto;
import com.todo.common.dto.TaskFilterDto;
import com.todo.common.dtoAsync.PageResponse;
import com.todo.task.entity.Task;
import com.todo.task.mapper.TaskMapper;
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


import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceAsync {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

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
        return taskRepository.saveAsync(task).thenApply(taskMapper::toDto);
    }

    public CompletableFuture<List<TaskDto>> find(@Valid TaskFilterDto request) {
        if (request.status() != null && request.userId() != null) {
            return taskRepository.findTaskByStatusAndUserId(request.status(), request.userId()).thenApply(taskMapper::toDtoList);
        }
        if (request.userId() != null) {
            return taskRepository.findTaskByUserId(request.userId()).thenApply(taskMapper::toDtoList);
        }
        if (request.status() != null) {
            return taskRepository.findTaskByStatus(request.status()).thenApply(taskMapper::toDtoList);
        }
        return CompletableFuture.completedFuture(Collections.emptyList());
    }
}


