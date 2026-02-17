package com.todo.gateway.filter;

import com.todo.common.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

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

            // Пропускаем публичные пути
            if (isPublicPath(request.getPath().toString())) {
                log.debug("Public path: {}", request.getPath());
                return chain.filter(exchange);
            }

            // Извлекаем токен
            String token = extractToken(request);
            if (token == null) {
                log.warn("No token found in request");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Проверяем токен
            if (!jwtService.validateToken(token)) {
                log.warn("Invalid token: {}", token);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Извлекаем данные из токена
            try {
                String email = jwtService.getEmailFromToken(token);
                Long userId = jwtService.getUserIdFromToken(token);
                String role = jwtService.getRoleFromToken(token);

                log.debug("Authenticated user: {}, role: {}", email, role);

                // Добавляем заголовки для downstream сервисов
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Email", email)
                        .header("X-User-Role", role)
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                log.error("Error processing token", e);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/auth/register") ||
                path.startsWith("/api/auth/login");
    }

    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    public static class Config {
        // Пустой конфиг, можно добавить параметры
        private boolean enabled = true;
        private List<String> publicPaths = List.of("/api/auth/**");

        // Геттеры и сеттеры
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public List<String> getPublicPaths() { return publicPaths; }
        public void setPublicPaths(List<String> publicPaths) {
            this.publicPaths = publicPaths;
        }
    }
}