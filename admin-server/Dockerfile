FROM eclipse-temurin:21-jre-jammy
ARG VERSION
WORKDIR /app
COPY admin-server/target/admin-server-${VERSION}.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","app.jar"]