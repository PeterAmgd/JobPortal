FROM openjdk:21-jdk-slim
LABEL authors="Peter"
WORKDIR /app
COPY target/JobPortal-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]