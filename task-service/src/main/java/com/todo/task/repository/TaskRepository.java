package com.todo.task.repository;

import com.todo.task.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {



    Page<Task> findByUserId(Long userId, Pageable pageable);


//    @Async
//    @Transactional
//    @Query("Select t from Task t where t.status=:status and t.userId= :userId")
//    CompletableFuture<List<Task>> findTaskByStatusAndUserId(@Param(value="status")String status,
//                                                               @Param(value = "userId") Long userId);
//    @Async
//    @Transactional
//    @Query("Select t from Task t where t.status=:status")
//    CompletableFuture<List<Task>> findTaskByStatus(@Param(value="status")String status);
//    @Async
//    @Transactional
//    @Query("Select t from Task t where t.userId = :userId")
//    CompletableFuture<List<Task>> findTaskByUserId(@Param(value = "userId") Long userId);
}