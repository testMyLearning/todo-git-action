
FROM eclipse-temurin:21-jre-jammy
ARG VERSION
COPY task-service/target/task-service-${VERSION}.jar /app.jar
COPY common-config /common-config/
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]