package com.todo.task.mapper;

import com.todo.common.dto.CreateTaskRequest;
import com.todo.common.dto.TaskDto;
import com.todo.common.dto.UpdateTaskRequest;
import com.todo.common.enums.StatusTask;
import com.todo.task.entity.Task;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = {StatusTask.class, LocalDateTime.class})
public interface TaskMapper {

    /**
     * Преобразование Entity → DTO
     */
    @Mapping(target = "status", expression = "java(task.getStatus().name())")
    TaskDto toDto(Task task);

    /**
     * Преобразование списка Entity → список DTO
     */
    List<TaskDto> toDtoList(List<Task> tasks);

    /**
     * Преобразование запроса на создание → Entity
     * userId передается отдельно, так как его нет в запросе
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "dateTimeOfCompletion", ignore = true)
    Task toEntity(CreateTaskRequest request);


}