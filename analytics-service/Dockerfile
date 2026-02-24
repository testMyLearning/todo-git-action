from eclipse-temurin:21-jdk-alpine AS build
workdir /app
ARG VERSION

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY common/pom.xml common/
COPY analytics-service/pom.xml analytics-service/
RUN chmod +x mvnw

# Скачиваем зависимости (кэширование)
RUN ./mvnw dependency:go-offline -B
# копируем проект
COPY common common
COPY analytics-service analytics-service

# Устанавливаем родительский POM
RUN ./mvnw clean install -N -DskipTests



# собираем проект
RUN ./mvnw -f analytics-service/pom.xml clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine
ARG VERSION
COPY --from=build /app/analytics-service/target/analytics-service-${VERSION}.jar /app.jar
COPY common-config /common-config/
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app.jar"]