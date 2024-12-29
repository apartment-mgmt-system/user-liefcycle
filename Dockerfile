# Stage 1: Build
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY . /app
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM openjdk:21-slim
COPY --from=build /app/target/UserLifecycleManagement-latest.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]