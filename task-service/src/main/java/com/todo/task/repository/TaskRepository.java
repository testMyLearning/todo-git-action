package com.todo.task.repository;

import com.todo.common.dto.TaskDto;
import com.todo.task.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByUserId(Long userId);

    List<Task> findByUserIdAndStatus(Long userId, String status);

    boolean existsByIdAndUserId(UUID id, Long userId);

    @Async
    @Transactional(readOnly = true)
    CompletableFuture<Page<Task>> findByUserIdAsync(Long userId, Pageable pageable);

    @Async
    @Transactional
    CompletableFuture<Task> saveAsync(Task task);
}