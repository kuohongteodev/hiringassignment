# Stage 1: Build the application
FROM maven:3.9.3-eclipse-temurin-21 as builder

WORKDIR /app

# Copy pom.xml and download dependencies (cache layers)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Package the app (skip tests to speed up)
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]