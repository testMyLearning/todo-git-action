package com.todo.analytics.config;

import com.todo.common.security.SecretService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
        HikariConfig config = new HikariConfig();

                config.setJdbcUrl("jdbc:postgresql://pgbouncer-analytics:5432/analytics_db");
//                config.setUsername(SecretService.getSecret("db_user"));
//                config.setPassword(SecretService.getSecret("db_password"));
        config.setUsername("postgres");
        config.setPassword("postgres");
               // config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(100);        // ← Сколько одновременных соединений
        config.setMinimumIdle(10);             // ← Минимум в простое
        config.setConnectionTimeout(5000);     // ← Таймаут ожидания (мс)
        config.setIdleTimeout(600000);         // ← Время простоя (мс)
        config.setMaxLifetime(1800000);        // ← Время жизни соединения
        config.setLeakDetectionThreshold(5000); // ← Поиск утечек

        // Важно для асинхронности!
//        config.setConnectionTestQuery("SELECT 1");
//        config.setPoolName("HikariPool-task");
        config.addDataSourceProperty("prepareThreshold", "0");
        config.addDataSourceProperty("preparedStatementCacheQueries", "0");

        return new HikariDataSource(config);

    }
}
