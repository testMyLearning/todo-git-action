package com.todo.gateway.controller;

import com.todo.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class FallbackController {


    @RequestMapping("/fallback/users")
    public Mono<ResponseEntity<ErrorResponse>> userServiceFallback() {
        log.error("fallback in user-service");

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "ошибка в в юзер сервис",
                "Попробуйте позже",
                "/fallback/users"
        );

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse));
    }

    @RequestMapping("/fallback/tasks")
    public Mono<ResponseEntity<ErrorResponse>> taskServiceFallBack() {
        log.error("fallback in task-service");

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "ошибка в в таск сервис",
                "Попробуйте позже",
                "/fallback/tasks"
        );

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse));
    }
}
