package com.todo.task.config;

import com.todo.common.security.SecretService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    @Bean
    @Primary
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource(){
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://postgres-task:5432/task_db")
                .username(SecretService.getSecret("db_user"))
                .password(SecretService.getSecret("db_password"))
                .driverClassName("org.postgresql.Driver")
                .build();

    }
}
