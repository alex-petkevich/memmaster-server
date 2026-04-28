FROM maven:3.9.11-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml ./
COPY src ./src
RUN mvn -Pprod -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app

# Ensure OS CA certificates (including Google's root CA) are up to date
# so outbound HTTPS calls (e.g. Google tokeninfo) succeed from the JVM.
RUN apt-get update -qq && apt-get install -y --no-install-recommends ca-certificates && \
    update-ca-certificates && \
    rm -rf /var/lib/apt/lists/*

COPY --from=builder /build/target/*.jar /app/app.jar

EXPOSE 8383
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

