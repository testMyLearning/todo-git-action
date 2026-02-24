package com.todo.analytics.service;

import com.todo.analytics.dto.DeadlineAlertDto;
import com.todo.analytics.entity.DeadlineAlert;
import com.todo.analytics.repository.DeadlineAlertRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Data
public class DeadlineAlertService {

    private final DeadlineAlertRepository repository;



    public List<DeadlineAlertDto> getTasksDeadlineInNext24Hours(){
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate today = LocalDate.now();
        List<DeadlineAlert> alerts = repository
                .findTasksWithDeadlineBetween(today, tomorrow);

        return alerts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    private DeadlineAlertDto toDto(DeadlineAlert alert) {
        return new DeadlineAlertDto(
                alert.getUserId(),
                alert.getTaskId(),
                alert.getTaskName(),
                alert.getDeadline()
        );
    }
    }


