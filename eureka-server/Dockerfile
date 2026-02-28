FROM eclipse-temurin:21-jre-jammy
ARG VERSION
WORKDIR /app

COPY eureka-server/target/eureka-server-${VERSION}.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]