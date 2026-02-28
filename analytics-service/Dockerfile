
FROM eclipse-temurin:21-jre-jammy
ARG VERSION
COPY analytics-service/target/analytics-service-${VERSION}.jar /app.jar
COPY common-config /common-config/
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app.jar"]