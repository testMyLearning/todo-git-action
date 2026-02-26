package com.todo.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.context.SecurityContextHolder;


@SpringBootApplication(scanBasePackages = {
        "com.todo.task",  // сканирует твой код
        "com.todo.common"    // сканирует код из common модуля!
})
public class TaskServiceApplication {
    public static void main(String[] args) {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        SpringApplication.run(TaskServiceApplication.class, args);
    }
}