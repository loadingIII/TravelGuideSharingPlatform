FROM node:20-alpine AS frontend-builder
WORKDIR /app
COPY frontend/package.json frontend/package-lock.json* ./
RUN npm ci
COPY frontend/ .
RUN npm run build

FROM maven:3.9-eclipse-temurin-17 AS backend-builder
WORKDIR /app
COPY --from=frontend-builder /app/dist /app/src/main/resources/static
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend-builder /app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
