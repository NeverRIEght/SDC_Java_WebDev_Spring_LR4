# Stage 1: Download Dependencies to cache them for futher builds
FROM maven:3.9.12-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Stage 2: Build project
COPY src ./src
RUN mvn package -DskipTests

# Stage 3: Deploy as a lightweight JRE
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY database ./database
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]