FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
ARG VERSION
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw

# Устанавливаем родительский POM
RUN ./mvnw clean install -N -DskipTests

# Устанавливаем common
COPY common common
RUN ./mvnw -f common/pom.xml clean install -DskipTests

# Собираем task-service
COPY task-service task-service
RUN ./mvnw -f task-service/pom.xml clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine
ARG VERSION
COPY --from=build /app/task-service/target/task-service-${VERSION}.jar /app.jar
COPY common-config /common-config/
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app.jar"]