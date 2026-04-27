FROM maven:3.9.11-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml ./
COPY src ./src
RUN mvn -Pprod -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /build/target/*.jar /app/app.jar

EXPOSE 8383
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

