# syntax=docker/dockerfile:1

# ---------- Build ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Copia primeiro o wrapper e o pom para aproveitar o cache de dependências
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src src
RUN ./mvnw -B -q package -DskipTests

# ---------- Runtime ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S atai && adduser -S atai -G atai
COPY --from=build --chown=atai:atai /workspace/target/atai-backend.jar app.jar
USER atai

ENV SPRING_PROFILES_ACTIVE=prod \
    PORT=8080 \
    JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
