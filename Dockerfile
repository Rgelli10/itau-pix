# Etapa 1: Build da aplicação
FROM maven:3.8.8-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Executar a aplicação
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/itau.pix-0.0.1-SNAPSHOT.jar /app/itau.pix.jar
ENTRYPOINT ["java", "-jar", "/app/itau.pix.jar"]