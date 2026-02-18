FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
ARG VERSION=1.0.0

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw

# Устанавливаем родительский POM
RUN ./mvnw clean install -N -DskipTests

# Устанавливаем common
COPY common common
RUN ./mvnw -f common/pom.xml clean install -DskipTests

# Собираем api-gateway
COPY api-gateway api-gateway
RUN ./mvnw -f api-gateway/pom.xml clean package -DskipTests



FROM eclipse-temurin:21-jre-alpine
COPY --from=build /app/api-gateway/target/api-gateway-${VERSION}.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]