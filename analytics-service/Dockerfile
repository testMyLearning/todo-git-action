FROM eclipse-temurin:21-jdk-alpine AS build
workdir /app
ARG VERSION

# Копируем Maven wrapper
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Копируем ВСЕ POM-файлы СРАЗУ
COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY analytics-service/pom.xml analytics-service/pom.xml

# Устанавливаем родительский POM (теперь видит все модули)
RUN ./mvnw clean install -N -DskipTests

# Копируем исходники common и устанавливаем
COPY common common
RUN ./mvnw -f common/pom.xml clean install -DskipTests

# Копируем исходники analytics-service и собираем
COPY analytics-service analytics-service
RUN ./mvnw -f analytics-service/pom.xml clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine
ARG VERSION
COPY --from=build /app/analytics-service/target/analytics-service-${VERSION}.jar /app.jar
COPY common-config /common-config/
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app.jar"]