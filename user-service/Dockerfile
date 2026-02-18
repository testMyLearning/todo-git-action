FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
ARG VERSION=1.0.0
# Копируем Maven wrapper и родительский POM
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw

# Устанавливаем родительский POM
RUN ./mvnw clean install -N -DskipTests

# Устанавливаем common
COPY common common
RUN ./mvnw -f common/pom.xml clean install -DskipTests

# Собираем user-service
COPY user-service user-service
RUN ./mvnw -f user-service/pom.xml clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine
COPY --from=build /app/user-service/target/user-service-${VERSION}.jar /app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]