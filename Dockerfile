# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy the parent pom
COPY pom.xml .

# Copy the modules
COPY mendel-api/ mendel-api/
COPY mendel-business/ mendel-business/
COPY mendel-dto/ mendel-dto/
COPY mendel-model/ mendel-model/
COPY mendel-util/ mendel-util/

# Build the project
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built jar from the api module (which is the Spring Boot application)
COPY --from=build /app/mendel-api/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
