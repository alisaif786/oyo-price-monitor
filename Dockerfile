# ============================================
# Stage 1: Build Spring Boot application
# ============================================

FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests


# ============================================
# Stage 2: Run Spring Boot + Playwright
# ============================================

FROM mcr.microsoft.com/playwright/java:v1.55.0-noble

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]