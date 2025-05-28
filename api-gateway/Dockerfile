# Etapa 1: Build con Maven
FROM maven:3.8.6-openjdk-11 AS builder

WORKDIR /build

# Copiar TODO el proyecto (necesario para resolver el parent pom)
COPY ./ ./

# Compilar solo el microservicio api-gateway
RUN mvn -pl api-gateway -am clean package -DskipTests

# Etapa 2: Imagen final ligera
FROM openjdk:11-jdk-slim

WORKDIR /home/app
ENV SPRING_PROFILES_ACTIVE=dev

# Copiar el JAR del microservicio
COPY --from=builder /build/api-gateway/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]
