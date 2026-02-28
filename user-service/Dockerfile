
FROM eclipse-temurin:21-jre-jammy
ARG VERSION
COPY user-service/target/user-service-${VERSION}.jar /app.jar
COPY common-config /common-config/
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]