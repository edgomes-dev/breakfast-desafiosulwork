# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copiando o JAR
COPY --from=build /app/target/*.jar app.jar

# Criando usuário não-root para segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080


ENTRYPOINT ["java", "-jar", "app.jar"]