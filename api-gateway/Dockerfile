
FROM eclipse-temurin:21-jre-alpine
ARG VERSION
COPY common-config /common-config/
COPY api-gateway/target/api-gateway-${VERSION}.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]