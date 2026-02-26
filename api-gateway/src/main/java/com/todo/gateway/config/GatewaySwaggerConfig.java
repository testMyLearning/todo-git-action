package com.todo.gateway.config;

import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class GatewaySwaggerConfig {

    @Bean
    @Primary
    public SwaggerUiConfigProperties swaggerUiConfigProperties(RouteDefinitionLocator locator) {
        SwaggerUiConfigProperties properties = new SwaggerUiConfigProperties();
        Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = new HashSet<>();

        // Блокируем получение списка маршрутов (выполняется один раз при старте)
        List<RouteDefinition> routeDefinitions = locator.getRouteDefinitions().collectList().block();

        if (routeDefinitions != null) {
            routeDefinitions.stream()
                    .filter(route -> route.getId().endsWith("-openapi"))
                    .forEach(route -> {
                        String name = route.getId().replace("-openapi", "");

                        String url = "/v3/api-docs/" + name;
                        urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl(name, url, name));
                    });
        }

        properties.setUrls(urls);
        return properties;
    }
}
