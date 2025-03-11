FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B


COPY src ./src
RUN mvn clean package -DskipTests -B

FROM openjdk:21
WORKDIR /app

COPY --from=build /app/target/*.jar backend-app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "backend-app.jar"]
