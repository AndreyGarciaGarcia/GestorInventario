# Dockerfile backend — GestorInventario (Spring Boot + MySQL)
# Multi-stage: compila con Maven 3.9, ejecuta con JRE 17 ligero

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -q -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=mysql
ENTRYPOINT ["java","-jar","app.jar"]
