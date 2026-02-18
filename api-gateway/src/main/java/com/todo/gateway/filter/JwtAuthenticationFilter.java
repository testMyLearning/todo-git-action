package com.todo.gateway.filter;

import com.todo.common.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Фильтр для проверки JWT токенов
 *
 * Зачем:
 * 1. Проверяет валидность JWT токена для защищенных путей
 * 2. Пропускает публичные пути (/api/auth/register, /api/auth/login)
 * 3. Извлекает данные пользователя и добавляет заголовки
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();

            // Пропускаем публичные пути (регистрация и вход)
            if (isPublicPath(path)) {
                log.debug("Public path: {}, skipping authentication", path);
                return chain.filter(exchange);
            }

            // Для защищенных путей - проверяем токен
            log.debug("Protected path: {}, checking authentication", path);

            // Извлекаем токен
            String token = extractToken(request);
            if (token == null) {
                log.warn("No token found for protected path: {}", path);
                return unauthorizedResponse(exchange, "No token provided");
            }

            // Проверяем валидность токена
            if (!jwtService.validateToken(token)) {
                log.warn("Invalid token for path: {}", path);
                return unauthorizedResponse(exchange, "Invalid token");
            }

            // Извлекаем данные из токена
            try {
                String email = jwtService.getEmailFromToken(token);
                Long userId = jwtService.getUserIdFromToken(token);
                String role = jwtService.getRoleFromToken(token);

                log.debug("Authenticated user: {}, role: {} for path: {}", email, role, path);

                // Добавляем заголовки для downstream сервисов
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Email", email)
                        .header("X-User-Role", role)
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                log.error("Error processing token for path: {}", path, e);
                return unauthorizedResponse(exchange, "Error processing token");
            }
        };
    }

    /**
     * Проверка, является ли путь публичным (не требует токена)
     */
    private boolean isPublicPath(String path) {
        return path.contains("/api/auth/register") ||
                path.contains("/api/auth/login");
    }

    /**
     * Извлечение JWT токена из заголовка Authorization
     */
    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Возврат 401 Unauthorized ответа
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    /**
     * Конфигурация фильтра (может быть расширена при необходимости)
     */
    public static class Config {
        // Можно добавить настройки, например:
        // private List<String> publicPaths;
        // private boolean enabled = true;
    }
}