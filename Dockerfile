# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the jar and skip tests for faster docker builds
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV MONGODB_URI=mongodb://mongodb:27017/deepseek
ENV OLLAMA_URL=http://ollama:11434

# Start command
ENTRYPOINT ["java", "-Dspring.data.mongodb.uri=${MONGODB_URI}", "-Dollama.api.url=${OLLAMA_URL}", "-jar", "app.jar"]
